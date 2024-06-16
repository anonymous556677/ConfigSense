import pandas as pd
import json
import os
from prompts import tasks, project_to_evaluate

def parse_json(file_path):
    with open(file_path, 'r') as file:
            data = json.load(file)
            #data = json.loads(data)
            return data

    
def cal_overall_accuracy(df, method):
    total_num = len(df)
    right_num = len(df[df[method] == df['Baseline']])
    accuracy = (right_num / total_num) * 100
    return f"{accuracy:.2f}%"

def cal_overall_precision(df, method):
    TP = len(df[(df[method] == 'Yes') & (df['Baseline'] == 'Yes')])
    FP = len(df[(df[method] == 'Yes') & (df['Baseline'] == 'No')])
    precision = TP / (TP + FP) * 100
    return f"{precision:.2f}%"
def calculate_overall_recall(df, method):
    TP = len(df[(df[method] == 'Yes') & (df['Baseline'] == 'Yes')])
    FN = len(df[(df[method] == 'No') & (df['Baseline'] == 'Yes')])
    recall = TP / (TP + FN) * 100
    return f"{recall:.2f}%"


def cal_sensitive_accuray(df, method, type):
    df = df[df['Baseline'] == type]
    total_num = len(df)
    right_num = len(df[df[method] == df['Baseline']])
    accuracy = (right_num / total_num) * 100
    return f"{accuracy:.2f}%"

def map_classification_result(result):
    result_dict = {"Performance-insensitive with 100% confidence": "No","No": "No", "Yes": "Yes", "Performance-sensitive with 100% confidence": "Yes", "1. Performance-insensitive with 100% confidence": "No"}
    return result_dict[result]

def benchmark_classification_result(result):
    result_dict = {'No': 'No', 'Yes': 'Yes'}
    return result_dict[result]

def get_data_df(directory, baseline):
    #tasks = ''
    directory = directory + f'/{tasks}/'
    all_dfs = []
    for index in range(1, 6):
        index = str(index)
        file_path = directory + f'{index}'
        df = get_df(file_path, baseline, index)
        all_dfs.append(df)
    combined_df = pd.concat(all_dfs, ignore_index=True)
    return combined_df



def get_df(directory, baseline, index):
    response_folder = directory
    item = 'PE/'
    data = []
    check_path = response_folder +'/' + item
    for filename in os.listdir(check_path):
        file_path = os.path.join(check_path, filename)
        metric_name = filename.split('.json')[0]
        if len(metric_name) > 0:
            try:
                metric_response = parse_json(file_path)
                metric_response['Configuration'] = metric_name
                data.append(metric_response)
            except:
                 print(metric_name + 'json cannot parse')

    response_df = pd.DataFrame(data)
    key = 'classification_result'
    response_df.rename(columns={f'{key}': 'LLM Context2'}, inplace=True)
    response_df['LLM Context'] = response_df['LLM Context2'].apply(map_classification_result)
    response_df = pd.merge(response_df, baseline, on='Configuration', how='left')
    response_df = response_df[(response_df['LLM Context'] == 'Yes' )|(response_df['LLM Context'] == 'No')]
    response_df = response_df.dropna(subset=['Baseline'])
    print("Overall LLM Context:" + cal_overall_accuracy(response_df, 'LLM Context'))
    print("Overall LLM precision:" + cal_overall_precision(response_df, 'LLM Context'))
    print("Overall Llm recall:" + calculate_overall_recall(response_df, 'LLM Context'))
    response_df['run times'] = index
    return response_df

#projects = ['dconverter']
#projects = ['catena']
#projects = ['batik']
#projects = ['sunflow']
#projects = ['prevayler']
#projects = ['h2']
projects = ['dconverter','catena', 'batik', 'sunflow', 'h2','cassandra']
projects = [f'{project_to_evaluate}']
for project in projects:
    data = []
    #baseline_path = '../data/analysis_result/cassandra_baseline.csv'
    baseline_path = '../data/analysis_result/baseline/' + project + '/baseline.csv'
    baseline = pd.read_csv(baseline_path)
    baseline['Baseline'] = baseline['Baseline'].apply(benchmark_classification_result)
    #projects = ['cassandra']
    response_folder = '../data/response_result/'
    item = 'graph_agent_new/PE/'
    #item = 'chatgpt/'
    file_path = "../data/response_result/" + project + "/graph_agent_new/" + 'rq2/'
    #file_path = "../data/response_result/" + project + "/chatgpt/"
    response_df = get_data_df(file_path, baseline)
    print(len(response_df))
    print("for the system: " + project)
    print(response_df[['Configuration','LLM Context', 'Baseline']])
    print("Overall LLM Context:" + cal_overall_accuracy(response_df, 'LLM Context'))
    print("Overall LLM precision:" + cal_overall_precision(response_df, 'LLM Context'))
    print("Overall Llm recall:" + calculate_overall_recall(response_df, 'LLM Context'))
    print('number of sensitive metrics: ' + str(len(response_df[response_df['Baseline'] == 'Yes'])))
    print("Sensitive LLM Context:" + cal_sensitive_accuray(response_df, 'LLM Context', 'Yes'))
    print("No Sensitive LLM Context:" + cal_sensitive_accuray(response_df, 'LLM Context', 'No'))
    #print("Sensitive DiaConfig:" + cal_sensitive_accuray(response_df, 'DiaConfig', 'Yes'))
    #print(len(response_df))
    if project == 'cassandra':
        print("Overall DiaConfig:" + cal_overall_accuracy(response_df, 'DiaConfig'))
        print("Overall DiaConfig precision:" + cal_overall_precision(response_df, 'DiaConfig'))
        print("Overall DiaConfig recall:" + calculate_overall_recall(response_df, 'DiaConfig'))
        print("Overall DiaConfig on entire data: " + cal_overall_accuracy(baseline, 'DiaConfig'))
        print("Sensitive DiaConfig:" + cal_sensitive_accuray(response_df, 'DiaConfig', 'Yes'))
        print("No Sensitive DiaConfig:" + cal_sensitive_accuray(response_df, 'DiaConfig', 'No'))
    response_df.to_csv('../data/analysis_result/result/' + project + '/result.csv', index=False)
    wrong_df = response_df[response_df['LLM Context'] != response_df['Baseline']]
    wrong_df['package_name'] = project_to_evaluate
    wrong_df.to_csv('../data/analysis_result/result/' + project + '/wrong_result.csv', index=False)
    wrong_df2 = wrong_df.drop_duplicates(subset=['Configuration'])
    wrong_df2.to_csv('../data/analysis_result/result/' + project + '/wrong_result_unique.csv', index=False)
    print("")
            

