import pandas as pd
from langchain_core.tools import tool
import json
from openai import OpenAI
from utils import load_api_key, get_completion, parse_yaml_comments, save_and_update_json_file, load_json, get_completion_gpt4
from setting import tasks, project_name, key_path


@tool
def extract_code_context_for_config(config_name):
    """
    This function is to help to extract code context for a configuration.
    """
    file_path = f'../data/method_context/{project_name}/{config_name}.csv'
    metric_context = pd.read_csv(file_path)
    code_context = metric_context['Method_body'].astype(str).str.cat(sep='\n')
    save_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
    save_and_update_json_file("code_context", code_context, save_file_path)
    return "Found and saved the code context to JSON file successfully."


@tool
def extract_unclear_method(unclear_method_name, config_name):
    """
    Extract unclear method inside the code context.
    unclear_method_name (str): The method name that you want to understand. The type of parameter is string. You can only understand one function each call. 
    config_name: it is the configuration name
    """
    file_path = f'../data/method_context/{project_name}_call_methods/{config_name}.csv'
    file_not_found = False
    try:
        called_method_context = pd.read_csv(file_path)
    except Exception as e:
        file_not_found = True
    if not file_not_found:
        method_names_dataset = dict(zip(called_method_context['Method_short'], called_method_context['Method_body']))
        method_names_map = dict(zip(called_method_context['Method_short'], called_method_context['Method']))
        method_names_benchmark = list(method_names_dataset.keys())
        unclear_method = "No found this Method-related information"
        # print(method_names_benchmark)
        # print(unclear_method_name)
        method_body = {}
        if isinstance(unclear_method_name, str):
            if '.' in unclear_method_name:
                unclear_method_name = unclear_method_name.split('.')[-1]
            for name in method_names_benchmark:
                if name in unclear_method_name or unclear_method_name in method_names_map[name]:
                    unclear_method = str(method_names_dataset[name])
                    break
    else:
        unclear_method = "No found this Method-related information"

    if isinstance(unclear_method_name, list):
        unclear_method = "Please only pass one unclear method name when you call this function"
    save_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
    #save_and_update_json_file(f"unclear_code_context", unclear_method, save_file_path)
    save_and_update_json_file(['unclear_method_name', 'unclear_method_body'], {"unclear_method_name": unclear_method_name, "unclear_method_body": unclear_method}, save_file_path)
    return unclear_method


@tool
def analyze_unclear_method(config_name):
    """
    after the unclear method is extracted, this function can provide analysis result about the unclear method
    config_name: the configuration name, not the method_name
    """
    api_key_path = key_path
    api_key = load_api_key(api_key_path)
    client = OpenAI(api_key=api_key)
    save_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
    result = load_json(save_file_path)
    code_context = result['code_context']
    #unclear_code = result['unclear_code_context']
    unclear_code = result['unclear_methods'][-1]['unclear_method_body']
    unclear_method_name = result['unclear_methods'][-1]['unclear_method_name']
    config_description = result.get('config_description','Cannot found this configuration description for this configuration.')
    prompt = {"Role": "You are a professional developer, your job is to understand the config-related code",
              "Requirement": "Understand the unclear code Output format requirement: Answer the questions 1 and 2, output the JSON format, the keys for answers 1 and 2 must be 'developer_understanding_on_unclear_method', 'developer_understanding_on_unclear_code_to_configuration",
              "Config related code": code_context,
              "unclear_code": unclear_code,
              "Configuration Description": config_description,
              "Question": "1. What is the unclear code about? "
                          "2. What is the relationship between unclear code and configuration?"}
    response = get_completion(client, json.dumps(prompt))
    # save_and_update_json_file(
    #     ["developer_understanding_on_unclear_method", "developer_understanding_on_unclear_code_to_configuration"],
    #     json.loads(response), save_file_path)
    save_data = {'unclear_method_name': unclear_method_name}
    if 'not found' not in unclear_code and 'no found' not in unclear_code:
        save_data.update({"understanding" : json.loads(response)})
    else:
        save_data.update({"understanding" : "No found this Method-related information"})
    #print(save_data)
    save_and_update_json_file(['unclear_method_name', "understanding"], save_data, save_file_path)
    return "Saved or updated the analysis result about the configuration to JSON file successfully. You can go next step."


@tool
def find_official_configuration_description(config_name):
    """
    Check the configuration description
    config_name(str): the configuration name
    """
    config_description = "Cannot found this configuration description for this configuration."
    # if project_name == 'catena':
    #     config_dict = pd.read_csv('../data/config_description/catena_description.csv').set_index('Configuration')['Description'].to_dict()
    #     print(config_name)
    #     if config_name in config_dict.keys():
    #         config_description = config_dict[config_name]
    #         print(config_description)
    #     save_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
    #     save_and_update_json_file("config_description", config_description, save_file_path)
    #     return "Found the configuration description to JSON file successfully. You can go next step."
    if project_name == 'h2':
        config_dict = pd.read_csv(f'../data/config_description/{project_name}_description.csv').set_index('Configuration')['Description'].to_dict()
        if config_name in config_dict.keys():
            config_description = config_dict[config_name]
        save_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
        save_and_update_json_file("config_description", config_description, save_file_path)
        return "Found the configuration description to JSON file successfully. You can go next step."
    if project_name != 'cassandra':
        config_description = "Cannot found this configuration description for this configuration."
        config_description = "Cannot found this configuration description for this configuration."
        save_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
        save_and_update_json_file("config_description", config_description, save_file_path)
        return "Found the configuration description to JSON file successfully. You can go next step."
    yaml_path = '../data/cassandra.yaml'
    with open(yaml_path, 'r') as file:
        content = file.read()
        config_dict = parse_yaml_comments(content, [config_name])
        if len(config_dict) == 0:
            config_description = "Cannot found this configuration description for this configuration."
            save_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
            save_and_update_json_file("config_description", config_description, save_file_path)
            return "Found the configuration description to JSON file successfully. You can go next step."
            #return config_description
        else:
            if config_name in config_dict.keys():
                config_description = config_dict[config_name]
            save_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
            save_and_update_json_file("config_description", config_description, save_file_path)
            return "Found the configuration description to JSON file successfully. You can go next step."

@tool
def filter_code_context_for_config(config_name):
    """
    This function is to help to filter code context for a configuration to get necessary code related config and
    save tokens.
    config_name: the configuration name
    """
    api_key_path = key_path
    api_key = load_api_key(api_key_path)
    client = OpenAI(api_key=api_key)
    read_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
    developer_feedback = load_json(read_file_path)
    code_context = developer_feedback['code_context']
    config_description = developer_feedback['config_description']
    prompt = {"Role": "You are a professional developer, your job is to filter the code that is very relevant to the configuration",
              "Requirement": "Understand the configuration and config-related code, You need to filter the code that are very relevant to the configuration for developers to understand the configuration, and remove the non-configuration-related parts. Testing configuration-related code is not necessary.",
              "Output format requirement": "Answer the question, output the JSON format, the key must be 'Filtered code context'",
              "Config related code": code_context,
              "Configuration Description": config_description,
              "Question": "What code is very relevant to the configuration?"}
    response = get_completion(client, json.dumps(prompt))
    save_and_update_json_file(['Filtered code context'], json.loads(response), read_file_path)
    return "Saved or updated the filtered code context to JSON file successfully. You can go next step."


@tool
def understand_configuration_code(config_name):
    """
    Understand the configuration code, include how the config works, the triggering frequency, and size impact.
    config_name: the configuration name
    """
    api_key_path = key_path
    api_key = load_api_key(api_key_path)
    client = OpenAI(api_key=api_key)
    save_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
    result = load_json(save_file_path)
    code_context = result['code_context']
    config_description = result['config_description']
    prompt = {"Role": "You are a professional developer, your job is to analyze and understand the config-related code",
              "Requirement": "Understand the configuration code, include how the config works in the code,"
                             "the triggering frequency of the configuration, and impact of the configuration option on the system.",
              "Output format requirement": "Answer the question, output the JSON format, the keys must be "
                                           "'developer_understanding_on_working', "
                                           "'developer_understanding_on_triggering_frequency', "
                                           "'developer_understanding_on_size_impact'",
              "Name of the configuration requested to be analyzed": config_name,
              "Config related code": code_context,
              "Configuration Description": config_description,
              "Question": "1. How the configuration works in the code?"}
                          #"2. How often the configuration is triggered in the system? (from the system level)"
                          #"3. What is the potential impact of the configuration option on system?"}
    response = get_completion(client, json.dumps(prompt))
    save_and_update_json_file(['developer_understanding_on_working', "developer_understanding_on_triggering_frequency",
                               "developer_understanding_on_size_impact"], json.loads(response), save_file_path)
    return "Saved or updated the analysis result about the configuration to JSON file successfully."


@tool
def check_if_needs_further_analysis_for_performance(config_name):
    """
    Check if there is any function is needed to further analyzed for conducting the performance analysis of configuration.
    If unclear function is found, you should ask another agent to help you to understand the unclear function.
    This tool cannot be used to analyze an unclear method. If you want to analyze an unclear method, you contact another agent to help you.
    config_name: the configuration name, it is not the method name. You should not pass method name to this function.
    """
    api_key_path = key_path
    api_key = load_api_key(api_key_path)
    client = OpenAI(api_key=api_key)
    read_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
    try:
        developer_feedback = load_json(read_file_path)
    except FileNotFoundError:
        return "The file is not found. Are you send the configuration name correctly? This function is used to check if the configuration has any unclear method. It is not used to analyze the unclear method. If you want to analyze the unclear method, you should contact another agent to help you."
    code_context = developer_feedback['code_context']
    config_description = developer_feedback['config_description']
    developer_understanding_on_working = developer_feedback['developer_understanding_on_working']
    developer_understanding_on_triggering_frequency = developer_feedback[
        'developer_understanding_on_triggering_frequency']
    developer_understanding_on_size_impact = developer_feedback['developer_understanding_on_size_impact']
    prompt = {
        "Role": "You are a professional performance engineer, your job is to analyze the performance of the configuration.",
        "Requirement": "You are going to conduct a performance analysis for the configuration."
                       "You need to check if any method in the code is unclear for you to conduct performance analysis."
                       "if there is any unclear method, you need to provide the method name for further explanation.",
        "Output format requirement": "Answer the question, output the JSON format, the key must be 'unclear method'",
        "Name of the configuration requested to be analyzed": config_name,
        "code context": code_context,
        "configuration description": config_description,
        "developer understanding on working": developer_understanding_on_working,
        "developer understanding on triggering frequency": developer_understanding_on_triggering_frequency,
        "developer understanding on size impact": developer_understanding_on_size_impact,
        "Question": "Is there any unclear method in the code for you to conduct performance analysis? (if there is Found_unclear_methods_info, please check the unclear method list first to make sure if the analysis of unclear method is already provided.)"
                    "If the answer is yes, please provide the method name. If there is no unclear method, answer 'No unclear method'."
    }
    unclear_methods_info = ""
    for method in developer_feedback.get('unclear_methods', []):
        method_name = method['unclear_method_name']
        method_body = method['unclear_method_body']
        try:
            if 'not found' not in method_body.lower() and 'no found' not in method_body.lower():
                understanding = method['understanding']['developer_understanding_on_unclear_method']
            else:
                understanding = "No found this Method-related information"
        except KeyError:
            return "Another agent forgot to analyze the unclear method. Please tell another agent to analyze the unclear method first"
        #configuration_relation = method['understanding']['developer_understanding_on_unclear_code_to_configuration']
        unclear_methods_info += f"Some unclear methods has been found from preivous analysis.There are the lists:\nunclear_method_name_has_been_found: {method_name}\nBody: {method_body}\nUnderstanding: {understanding}\n If you have new unclear method, please check if it is already found in the unclear method list. If they have been found, please check other parts. If there is no other unclear methods, reply No unclear method"
    if len(unclear_methods_info) > 0:
        prompt['Found_unclear_methods_info'] = unclear_methods_info
    #print(prompt)
    response = get_completion(client, json.dumps(prompt))
    if json.loads(response)['unclear method'] == 'No unclear method':
        return "There is no unclear method. You can calculate the probability of performance impact of the configuration."
    return ("There is unclear method name is: " + "".join(json.loads(response)['unclear method'])
            + ". Please send a request to another agent and ask another agent to help you to analyze the unclear "
              "method. Do not call check_if_needs_further_analysis_for_performance tool to analyze")


@tool
def calculate_probability_of_performance_impact(config_name):
    """
    Calculate the probability of performance impact of the configuration. This function can be called only after check_if_needs_further_analysis_for_performance was called before.
    config_name: the configuration name
    project_name: the project name
    """
    api_key_path = key_path
    api_key = load_api_key(api_key_path)
    client = OpenAI(api_key=api_key)
    save_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/PE/' + config_name + '.json'
    read_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
    developer_feedback = load_json(read_file_path)
    code_context = developer_feedback['code_context']
    #config_description = developer_feedback['config_description']
    developer_understanding_on_working = developer_feedback['developer_understanding_on_working']
    developer_understanding_on_triggering_frequency = developer_feedback['developer_understanding_on_triggering_frequency']
    developer_understanding_on_size_impact = developer_feedback['developer_understanding_on_size_impact']
    prompt = {
        "Role": "You are a professional performance engineer, you are required to classify the performance sensitivity of the configuration.",
        "Requirement": "Based on config-related code context and provided information from developer, You are required to classify if the configuration is performance-sensitive or not."
                       "The developer's information can be used as a reference, but more emphasis should be placed on analyzing the configuration-related code when you do the classification."
                       "A performance-sensitive configuration leads to notable performance variations, defined as operations that cause significant performance variation from the system level."
                       "Performance-related operations refer to code snippets in a program contributing positively to execution time (time-expensive) and memory consumption (memory-expensive)."
                       "Since not all performance-related operations have a significant performance impact, some configurations may have trivial performance impact, which should not be considered performance-sensitive."
                       "A configuration is considered performance-sensitive if it directly and substantially affects system performance at the system level, rather than causing trivial or moderate performance impacts"
                       "Additionally, a configuration must involve some performance-related operations that are time-extensive or memory-extensive within its related code and lead to notable performance variations at the system level",
        "Output format requirement": "Answer the question, output the JSON format, the keys must be 'classification_result', 'Reason'",
        "Name of the configuration requested to be analyzed": config_name,
       "code context": code_context,
        #"configuration description": config_description,
        "developer understanding on working": developer_understanding_on_working,
        "developer understanding on triggering frequency": developer_understanding_on_triggering_frequency,
        "developer understanding on size impact": developer_understanding_on_size_impact,
        "Question":   "1. Is the configuration option performance-sensitive? (classification_result should be Yes or No.)"
                      "2. What is the reason for your classification? (Explain reason from the system level and the perspective of configuration-related code)"
             }
    unclear_methods_info = ""
    for method in developer_feedback.get('unclear_methods', []):
        method_name = method['unclear_method_name']
        method_body = method['unclear_method_body']
        try:
            if 'not found' not in method_body.lower() and 'no found' not in method_body.lower():
                understanding = method['understanding']['developer_understanding_on_unclear_method']
                configuration_relation = method['understanding']['developer_understanding_on_unclear_code_to_configuration']
            else:
                understanding = "No found this Method-related information"
                configuration_relation = "No found this Method-related information"
        except KeyError:
            return "Another agent forgot to analyze the unclear method. Please tell another agent to analyze the unclear method first."
        unclear_methods_info += f"Method: {method_name}\nBody: {method_body}\nUnderstanding: {understanding}\nConfiguration Relation: {configuration_relation}\n\n"
    if len(unclear_methods_info) > 0:
        prompt['unclear_methods_info'] = unclear_methods_info
    if 'unclear_code_context' in developer_feedback and developer_feedback.get(
            'unclear_code_context') != "No found this Method-related information":
        prompt['unclear_code'] = developer_feedback['unclear_code_context']
        prompt['developer_understand_on_unclear_method'] = developer_feedback[
            'developer_understanding_on_unclear_method']
        prompt['developer_understanding_on_unclear_code_to_configuration'] = developer_feedback[
            'developer_understanding_on_unclear_code_to_configuration']
    response = get_completion(client, json.dumps(prompt))
    with open(save_file_path, 'w') as file:
        json.dump(json.loads(response), file, indent=4)
    return "calculation is done and the result is saved. Please end the task."

@tool
def extract_code_context_from_json(config_name):
    """
    Extract code context from developer feedback JSON file
    config_name (str): the configuration name
    """
    read_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
    developer_feedback = load_json(read_file_path)
    code_context = developer_feedback['code_context']
    return code_context


@tool
def extract_description_from_json(config_name):
    """
    extract configuration description from developer feedback JSON file
    config_name (str): the configuration name
    """
    read_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
    developer_feedback = load_json(read_file_path)
    description = developer_feedback['config_description']
    return description


@tool
def initial_classification_of_configuration_performance(config_name):
    """
    Classify the performance of the configuration
    config_name: the configuration name
    """
    api_key_path = key_path
    api_key = load_api_key(api_key_path)
    client = OpenAI(api_key=api_key)
    read_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/DE/' + config_name + '.json'
    developer_feedback = load_json(read_file_path)
    config_description = developer_feedback['config_description']
    if len(config_description) == 0 or config_description == "Cannot found this configuration description for this configuration.":
        config_description = "The configuration description is not provided."
    prompt = {
        "Role": "You are a professional performance engineer, your job find the configuration option that is performance-sensitive or not.",
        "Instrument": "Based on your knowledge and the configuration description in documentation, you can anaylze the configuration is performance-insensitive or needs further analysis to decide. The function or impact of configuration may be a clue to make a decision. For example, configuration related to name is apparently performance-insensitive since it is the only the name of a cluster. Different usernames don't impact system performance.",
        "Requirement and must read and follow carefully":"If the configuration is related to the duration, timeout, setting of cache, optimizing important system function, thread, or other potential memeory-related operations cannot be classified as performance-insensitive with 100% confidence since they need further anaylsis to decide the performance sensitivity.",
        "Output format requirement": "Answer the question, output the JSON format, the key must be 'classification_result', 'Reason' ",
        "configuration description": config_description,
        "Question": "Is the configuration option is performance-insensitive?"
                    "What is the reason for your classification? ",
        "Note": "The answer for the questions should be: 1. Performance-insensitive with 100% confidence, 2.  May be performance-sensitive or insensitive. Further analysis to decide the performance sensitivity. "
                "When you answer for configuration option is performance-insensitive with 100% confidence, you must be very carefully and ensure that the configuration option does not involve performance-related operations or affect system performance, and is unrelated to any time-dependent aspects of system operation."
                "If the configuration description is not provided, you should answer 'May be performance-sensitive or insensitive. Further analysis to decide the performance sensitivity.'"
                #"When you answer for configuration option is performance-sensitive with 100% confidence, you must be carefully and make sure the configuration option has performance impact on the system and the impact must be significant."
    }
    response = get_completion(client, json.dumps(prompt))
    classification_result = json.loads(response)['classification_result']
    save_file_path = f'../data/response_result/{project_name}/graph_agent_new' + f'/rq2/{tasks}/1/' + '/PE/' + config_name + '.json'
    if "100% confidence" in classification_result:
        with open(save_file_path, 'w') as file:
            json.dump(json.loads(response), file, indent=4)
    return classification_result

