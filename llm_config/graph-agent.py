import getpass
import os
import json

import pandas as pd
from langchain_core.messages import (
    AIMessage,
    BaseMessage,
    ChatMessage,
    FunctionMessage,
    HumanMessage,
)
from langchain.tools.render import format_tool_to_openai_function
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langgraph.graph import END, StateGraph
from langgraph.prebuilt.tool_executor import ToolExecutor, ToolInvocation
import functools
from langchain_core.tools import tool
from tool_config import extract_code_context_for_config, extract_unclear_method, find_official_configuration_description, understand_configuration_code, check_if_needs_further_analysis_for_performance, calculate_probability_of_performance_impact, analyze_unclear_method, initial_classification_of_configuration_performance
#from typing import Annotated
#from langchain_experimental.utilities import PythonREPL
#from langchain_community.tools.tavily_search import TavilySearchResults
import operator
from typing import Annotated, List, Sequence, Tuple, TypedDict, Union
from langchain.agents import create_openai_functions_agent
from langchain.tools.render import format_tool_to_openai_function
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_openai import ChatOpenAI
from typing_extensions import TypedDict
import random
import prompts
import setting
from utils import write_fail_option_to_file



def _set_if_undefined(var: str):
    if not os.environ.get(var):
        os.environ[var] = getpass(f"Please provide your {var}")


def create_agent(llm, tools, system_message: str):
    """Create an agent."""
    functions = [format_tool_to_openai_function(t) for t in tools]

    prompt = ChatPromptTemplate.from_messages(
        [
            (
                "system",
                "You are a helpful AI assistant, collaborating with other assistants."
                " Use the provided tools to progress towards answering the question."
                " If you are unable to fully answer, that's OK, another assistant with different tools "
                " will help where you left off. Execute what you can to make progress."
                " If you or any of the other assistants have the final answer or deliverable,"
                " You don't need to thank you for each other once the work is done."
                " You have access to the following tools: {tool_names}.\n{system_message}",
            ),
            MessagesPlaceholder(variable_name="messages"),
        ]
    )
    prompt = prompt.partial(system_message=system_message)
    prompt = prompt.partial(tool_names=", ".join([tool.name for tool in tools]))
    return prompt | llm.bind_functions(functions)


class AgentState(TypedDict):
    messages: Annotated[Sequence[BaseMessage], operator.add]
    sender: str

def agent_node(state, agent, name):
    result = agent.invoke(state)
    last_message = state["messages"][-1]
    if isinstance(result, FunctionMessage):
        pass
    else:
        result = HumanMessage(**result.dict(exclude={"type", "name"}), name=name)
    return {
        "messages": [result],
        "sender": name,
    }


def tool_node(state):
    messages = state["messages"]
    last_message = messages[-1]
    tool_input = json.loads(
        last_message.additional_kwargs["function_call"]["arguments"]
    )
    if len(tool_input) == 1 and "__arg1" in tool_input:
        tool_input = next(iter(tool_input.values()))
    tool_name = last_message.additional_kwargs["function_call"]["name"]
    action = ToolInvocation(
        tool=tool_name,
        tool_input=tool_input,
    )
    response = tool_executor.invoke(action)
    function_message = FunctionMessage(
        content=f"{tool_name} response: {str(response)}", name=action.tool
    )
    return {"messages": [function_message]}


def router(state):
    messages = state["messages"]
    last_message = messages[-1]
    if "function_call" in last_message.additional_kwargs:
        return "call_tool"
    if "FINAL ANSWER" in last_message.content:
        return "end"
    return "continue"

def to_serializable(obj):
    if isinstance(obj, HumanMessage):
        return {"content": obj.content}  
    return str(obj)

_set_if_undefined("OPENAI_API_KEY")
llm = ChatOpenAI(model="gpt-3.5-turbo-0125", temperature=0.3)
#llm = ChatOpenAI(model="gpt-4", temperature=0.3)
system_message_developer = prompts.system_message_developer
system_message_performance = prompts.system_message_performance
developer_agent = create_agent(
    llm,
    [extract_code_context_for_config, extract_unclear_method, find_official_configuration_description, understand_configuration_code, analyze_unclear_method],
    system_message=system_message_developer,
)
developer_node = functools.partial(agent_node, agent=developer_agent, name="Developer")

# Performance agent
performance_agent = create_agent(
    llm,
    [check_if_needs_further_analysis_for_performance, calculate_probability_of_performance_impact, initial_classification_of_configuration_performance],
    system_message=system_message_performance,
)
performance_node = functools.partial(
    agent_node, agent=performance_agent, name="Performance_Expert"
)

tools = [extract_code_context_for_config, extract_unclear_method, find_official_configuration_description, understand_configuration_code, check_if_needs_further_analysis_for_performance, calculate_probability_of_performance_impact, analyze_unclear_method, initial_classification_of_configuration_performance]
tool_executor = ToolExecutor(tools)

workflow = StateGraph(AgentState)

workflow.add_node("Developer", developer_node)
workflow.add_node("Performance_Expert", performance_node)
workflow.add_node("call_tool", tool_node)

workflow.add_conditional_edges(
    "Developer",
    router,
    {"continue": "Performance_Expert", "call_tool": "call_tool", "end": END},
)
workflow.add_conditional_edges(
    "Performance_Expert",
    router,
    {"continue": "Developer", "call_tool": "call_tool", "end": END},
)

workflow.add_conditional_edges(
    "call_tool",
    lambda x: x["sender"],
    {
        "Developer": "Developer",
        "Performance_Expert": "Performance_Expert",
    },
)
workflow.set_entry_point("Developer")
graph = workflow.compile()
option_names = []
#project = 'cassandra'
#project = 'prevayler'
project = 'dconverter'
#project = 'batik'
#project = 'catena'
#project = 'sunflow'
#project = 'h2'
random.seed(10)
option_path = f'../data/method_context/{project}/'
for filename in os.listdir(option_path):
    if filename.endswith(".csv"):
        option_names.append(filename.split('.csv')[0])

result_file_path = f'../data/response_result/' + project + f'/graph_agent_new/rq2/{setting.tasks}/1/PE/'
option_names_processed = []
for filename in os.listdir(result_file_path):
    if filename.endswith(".json"):
        option_names_processed.append(filename.split('.json')[0])
option_names = list(set(option_names) - set(option_names_processed))
print(len(option_names))
print(option_names)
index = 0
#option_names = ['key_cache_size_in_mb']
for option_name in option_names:
    message = f"Target configuration:  {option_name}"
    input = {"messages": [HumanMessage(content=message)]}
    try:
        result_conversation = graph.invoke(input, {"recursion_limit": 60})
        # result_conversation['messages'] = [f"{x.type} +=+ {x.name} +=+ {x.content}" for x in result_conversation['messages']]
        # with open(f'../data/response_result/{project}/graph_agent_new/conversation/{option_name}.json', 'w') as f:
        #     f.write(json.dumps(result_conversation))
    except Exception as e:
        print("failed: " + str(option_name))
        print(e)
        fail_save_path = f'../data/response_result/{project}/graph_agent_new/' + 'fail_options.csv'
        write_fail_option_to_file(option_name, project, fail_save_path)
    index += 1
    print("processed: " + str(index))



#
# for s in graph.stream(
#     {
#         "messages": [
#             HumanMessage(
#                 content="Target configuration: ALLOWED_SCRIPT_TYPES"
#             )
#         ],
#     },
#     # Maximum number of steps to take in the graph
#     {"recursion_limit": 50},
#  ):
#     print(s)
#     print("----")

