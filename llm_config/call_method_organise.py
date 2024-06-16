import pandas as pd
import os
import ast
import subprocess
# from javalang.parse import parse
# from javalang.tree import MethodDeclaration
# import javalang as jl
#import xml.etree.ElementTree as ET
from lxml import etree as ET

def extract_method_name(full_name):
    return full_name.split(':')[-1].split('(')[0]

def get_method_file_path(directory, method):
    class_name = ''
    class_path = ''
    if method.count(':') == 2:
        class_path = method.split(':')[1].replace('.','/')
        class_name = method.split(':')[1].split('.')[-1]
        if '$' in class_path:
            class_path = class_path.split('$')[0]
            class_name = method.split(':')[1].split('.')[-1]
            if '$' in class_name:
                class_name = class_name.split('$')[0]
    else:
        class_path = method.split(':')[0].split(')')[1].replace('.','/')
        class_name = method.split(':')[0].split('.')[-1]
        if '$' in class_name:
            class_name = class_name.split('$')[0]
    print(class_name)
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file == class_name + '.java' and '/'.join(class_path.split('/')[:-1]) in root:
                path = os.path.join(root, file)
                return path
    return 'cannot find'

def generate_xml_fle(file_path, project, xml_output_directory):
    file_name = file_path.split('/')[-1].split('.')[0]
    xml_path = xml_output_directory + f"{project}_call_methods" + '/' + file_name + ".xml"
    command_to_xml = 'srcml ' + file_path + ' -o ' + xml_path
    os.system(command_to_xml)
    return xml_path

        
def extract_source(srcml_file, method_name, class_name):
    # Load the srcML file
    result = 'not found'
    try:
        tree = ET.parse(srcml_file)
        root = tree.getroot()
        # Define the namespace to find elements
        ns = {'src': 'http://www.srcML.org/srcML/src'}
        if '<init>' in method_name:
            if has_inner_element(class_name):
                search_target = get_target_name(class_name)
                result = extract_body(root, ns, '*[src:name]', search_target)
            else:
                result = extract_body(root, ns, 'constructor',  class_name)
        # if 'build' in method_name:
        #     search_target = 'Benchmark'
        #     result = extract_body(root, ns, '*[src:name]', search_target)

        elif '<clinit>' in method_name:
            if has_inner_element(class_name):
                search_target = get_target_name(class_name)
                result = extract_body(root, ns, '*[src:name]', search_target)
            else:
                result = extract_body(root, ns, 'class', class_name)
        elif '$' in method_name:
            if has_inner_element(class_name):
                search_target = get_target_name(class_name)
                result = extract_body(root, ns, '*[src:name]', search_target)
            else:
                result = extract_body(root, ns, 'class', class_name)
        else:
            result = extract_body(root, ns, 'function', method_name)
        return result
    except Exception as e:
        return result
    # Search for the function/method within the XML structure
def has_inner_element(class_name):
    if '$' in class_name:
        return True
    else:
        return False

def get_target_name(class_name):
    start = class_name.split('$')[0]
    end = class_name.split('$')[1]
    if end.isdigit():
        return start
    elif end[0].isdigit():
        #return end[1:]
        return start
    else:
        #return end
        return start
def extract_body(root, ns, element_type, search_name):
    function_texts = []
    for function in root.findall(f".//src:{element_type}", ns):
        name = function.find("src:name", ns)

        if name is not None and name.text == search_name:
            # Initialize the source code string with an empty value
            comments = []
            sibling = function.getprevious()
            while sibling is not None and sibling.tag.endswith('comment'):
                comment_text = "".join(sibling.itertext())
                comments.append(comment_text.strip())
                sibling = sibling.getprevious()
            comments.reverse()
            function_text = ET.tostring(function, encoding='unicode', method='text')
            function_texts.append("\n".join(comments) + '\n' + function_text)
    if len(function_texts) == 0:
        return 'not found'
    else:
        if len(function_texts) > 1:
            return "\n".join(function_texts)
        else:
            return ''.join(function_texts)

def extract_class_name(method):
    class_name = ''
    if method.count(':') == 2:
        class_name = method.split(':')[1].split('.')[-1]
    else:
        class_name = method.split(':')[0].split('.')[-1]
    return class_name


#projects = ['dconverter', 'prevayler']
projects = ['cassandra']
#projects = ['catena']
#projects = ['sunflow']
#projects = ['batik']
#projects = ['h2']
project_path = {
                'cassandra':'../data/system/cassandra/src/java/',
                'h2':'../data/system/h2database/h2/',
                'dconverter':'../data/system/density-converter/',
                'prevayler':'../data/system/prevayler/',
                'batik': '../data/system/xmlgraphics-batik/',
                'sunflow': '../data/system/sunflow/',
                'catena': '../data/system/catena/'
}

for project in projects:
    xml_path = '../data/xml/'
    directory = project_path[project]
    metric_output_directory = '../data/method_context/'
    call_graph_path = '../data/call_graph/' + project + '/final.csv'
    call_graph_df = pd.read_csv(call_graph_path)
    if project == 'cassandra':
        call_graph_df = call_graph_df[call_graph_df['Called_Method'].str.contains('org.apache.cassandra.')]
    #call_graph_df = call_graph_df[~call_graph_df['Called_Method'].str.contains('java.')]
    print(call_graph_df)
    option_file_path = '../config/location/' + f'{project}_location.csv'
    option_df = pd.read_csv(option_file_path)
    option_df = option_df.drop_duplicates().drop(columns=['Called_Method'])
    option_df = pd.merge(option_df, call_graph_df, on='Method', how='left')
    option_df.to_csv("../config/" + project + "/full_location_pure_tmp.csv", index=False)
    option_df = option_df.drop(columns=['Method']).rename(columns={'Called_Method':'Method'})
    # print(len(option_df))
    option_df['Method_short'] = option_df['Method'].apply(extract_method_name)
    #exclude = ['java.','com.google.']
    exclude = [
        'java.io',
        'java.util',
        'java.lang',
        'java.math',
        'java.net',
        'java.security',
        'java.sql',
        'java.awt',
        'java.beans',
        'java.nio',
        'java.text',
        'java.time',
        'java.rmi',
        'java.applet',
        'java.lang.reflect',
        'java.lang.annotation',
        'javax.crypto',
        'javax.net',
        'javax.xml',
        'javax.swing'
        'com.google',
    ]
    option_df = option_df[~option_df['Method'].str.contains('|'.join(exclude))]
    option_df = option_df.drop_duplicates()
    option_df['path'] = option_df['Method'].apply(lambda x : get_method_file_path(directory, x))
    condition = option_df['path']!='../data/system/cassandra/src/java/org/apache/cassandra/net/VerbTimeouts.java'
    option_df = option_df[condition]
    option_df['class_name'] = option_df['Method'].apply(lambda x : extract_class_name(x))
    option_df['xml_path'] = option_df['path'].apply(lambda x: generate_xml_fle(x, project, xml_path))
    option_df['Method_body'] = option_df.apply(lambda row: extract_source(row['xml_path'], row['Method_short'],row['class_name']), axis=1)
    option_df.to_csv('../config/' + project +'/full_location_pure_called.csv', index=False)

    for option in option_df['option'].tolist():
        option = str(option)
        specific_option_df = option_df[option_df['option'] == option]
        specific_option_df.to_csv(metric_output_directory + f'{project}_call_methods/' + option + '.csv', index=False)



