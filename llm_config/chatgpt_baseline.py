from utils import get_completion, load_api_key
from openai import OpenAI
import os
import json
from setting import key_path




projects = ['dconverter','catena','batik','sunflow','prevayler','h2', 'cassandra']
#projects = ['batik','sunflow','prevayler','h2', 'cassandra']
#projects = ['cassandra']
api_key_path = key_path
api_key = load_api_key(api_key_path)
client = OpenAI(api_key=api_key)

for project_name in projects:
    option_path = '../config/config_analysis_list/' + project_name + '/'
    option_names = []
    for filename in os.listdir(option_path):
        if filename.endswith(".json"):
            option_names.append(filename.split('.json')[0])
    print(option_names)
    for index in range(1, 6):
        for config_name in option_names:
            save_file_path = f'../data/response_result/{project_name}/chatgpt/{index}/' + config_name + '.json'
            prompt = {
                "Role": "You are a professional performance engineer, you are required to classify the performance sensitivity of the configuration.",
                #"Requirement": "Determine if the given software configuration is performance-sensitive. A configuration is deemed performance-sensitive if it leads to notable variations in performance at the system level. This includes operations that significantly affect execution time or memory consumption. Ignore configurations causing only trivialperformance impacts.",
                "Requirement": "Determine if the given software configuration is performance-sensitive based on the impact of its "
                "value change on system performance. A configuration is deemed performance-sensitive if its value "
                "change significantly affects system performance, such as causing notable variations in execution "
                "time or memory consumption.",
                "Output format requirement": "Answer the question, output the JSON format, the keys must be 'classification_result', 'Reason'",
                "Name of the configuration requested to be analyzed": config_name,
                "software project name": project_name,
                "Question": "1. Is the configuration option performance-sensitive? (classification_result should be Yes or No.)"
                            "2. What is the reason for your classification? (Explain reason from the system level)"
            }
            response = get_completion(client, json.dumps(prompt))
            with open(save_file_path, 'w') as file:
                json.dump(json.loads(response), file, indent=4)
        print(f'Finish project {project_name}')
