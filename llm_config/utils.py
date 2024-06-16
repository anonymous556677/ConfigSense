import json
import pandas as pd
import os


def load_api_key(key_path):
    with open(key_path, 'r') as file:
        data = json.load(file)
        return data['Key']


def get_completion(client, prompt):
    messages = [{"role": "user", "content": prompt}]
    response = client.chat.completions.create(
        model="gpt-3.5-turbo-0125",
        #model="gpt-4",
        messages=messages,
        response_format={"type": "json_object"},
        temperature=0.3,
    )
    return response.choices[0].message.content

def get_completion_gpt4(client, prompt):
    messages = [{"role": "user", "content": prompt}]
    response = client.chat.completions.create(
        model="gpt-4",
        messages=messages,
        temperature=0.3,
    )
    return response.choices[0].message.content


def get_method_context(file_path):
    code_data = pd.read_csv(file_path)
    code_context = code_data['Method_body'].astype(str).str.cat(sep=' ')
    return code_context


def parse_yaml_comments(yaml_str, config_list):
    # Split the content by lines
    lines = yaml_str.strip().split("\n")
    config_dict = {}
    comment_lines = []

    for line in lines:
        # Check if the line is a comment
        if line.strip().startswith("#") and not check_in_list(line, config_list):
            comment_lines.append(line.strip("#").strip())
        elif check_in_list(line, config_list):
            config_key = line[1:].strip().split(":")[0].strip()
            comment = " ".join(comment_lines)
            # Add to the dictionary
            config_dict[config_key] = comment
            # Reset comment_lines for the next entry
            comment_lines = []
        else:
            # Extract the configuration key
            config_key = line.split(":")[0].strip()
            # Join the collected comment lines into a single string
            comment = " ".join(comment_lines)
            # Add to the dictionary
            config_dict[config_key] = comment
            # Reset comment_lines for the next entry
            comment_lines = []

    return config_dict

def check_in_list(target_str, config_list):
    for config in config_list:
        if config in target_str:
            target_str = target_str[1:].strip().split(":")[0].strip()
            if config == target_str:
                return True

def save_and_update_json_file(json_key, save_data, file_path):
    data = {}
    if os.path.exists(file_path):
        with open(file_path, 'r') as file:
            data = json.load(file)
    else:
        data["unclear_methods"] = []
    if isinstance(json_key, list):
        if 'unclear_method_name' in json_key and 'unclear_method_body' in json_key:
            method_names = [method['unclear_method_name'] for method in data['unclear_methods']]
            if save_data['unclear_method_name'] not in method_names:
                data['unclear_methods'].append(save_data)
        elif 'unclear_method_name' in json_key and 'understanding' in json_key:
            for item in data['unclear_methods']:
                if item['unclear_method_name'] == save_data['unclear_method_name']:
                    item['understanding'] = save_data['understanding']
        else:
            for key in json_key:
                data[key] = save_data[key]
    else:
        data[json_key] = save_data
    with open(file_path, 'w') as file:
        json.dump(data, file, indent=4)

def load_json(file_path):
    with open(file_path, 'r') as file:
        data = json.load(file)
        return data

def write_fail_option_to_file(option_name, project, file_path):
    with open(file_path, 'a') as file:
        file.write(f"{option_name},{project}\n")
