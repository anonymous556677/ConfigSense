import pandas as pd
from retrieve_context_xml import extract_source, extract_body, extract_class_name, extract_method_name, get_method_file_path, generate_xml_fle
import re
def seperate(function_name):
    if isinstance(function_name, str):
        if ')' in function_name:
            return ')'.join(function_name.split(')')[1:])
        else:
            return function_name
    else:
        return function_name

def convert_to_camel_case(text):
    parts = text.lower().split('_')
    return parts[0] + ''.join(x.capitalize() for x in parts[1:])

def from_camel_case(s):
    return ''.join('_' + c if c.isupper() else c for c in s).upper()

def check_and_transform(option, option_list, text):
    option_list = [convert_to_camel_case(option) for option in option_list]
    #print(option_list)
    if option!= 'return setting instance':
        return 'useless'
    # if option!= 'return setting instance' and convert_to_camel_case(option) not in option_list:
    #     return 'useless'
    # elif option!= 'return setting instance' and convert_to_camel_case(option) in option_list:
    #     return option
    else:
        pattern = r'getSettings\(\)\.(\w+)'
        matches = re.findall(pattern, text)
        transformed = 'useless'
        for match in matches:
            transformed = re.sub(r'([A-Z])', r'_\1', match).upper()
        if transformed == 'useless':
            option_to_save = []
            for option in option_list:
                if option in text:
                    option_to_save.append(from_camel_case(option))
            return "".join(option_to_save)
        return transformed

def get_option_name(method_body):
    matches = re.findall(r'getInt\("([^"]+)"', method_body)
    return ",".join(matches)

def get_config_batik(option, method_body):
    if option != 'access config':
        return option
    pattern = r"get\s*\(\s*(?:[\w\.]*\.)?([\w_]+)\s*\)"
    matches = re.findall(pattern, method_body)
    return ",".join(matches)

def parse_option_batik(option):
    if 'KEY' not in option:
        option = from_camel_case(option)
    else:
        option = option.split('KEY_')[1]
    return option

#projects = ['cassandra', 'h2', 'dconverter', 'prevayler', 'batik', 'sunflow', 'catena', 'kanzi']
project_path = {'h2':'../data/system/h2database/h2/src/main/',
                'sunflow': '../data/system/sunflow/',
                'batik': '../data/system/xmlgraphics-batik/',}
projects =['h2']
#projects = ['sunflow']
#projects = ['batik']
for project_name in projects:
    config_data = pd.read_csv(f'../config/config_extract_list/{project_name}.csv')
    graph_data = pd.read_csv(f'../data/call_graph/{project_name}/final.csv')
    graph_data['function'] = graph_data['Called_Method'].apply(seperate)
    involved_method = pd.merge(graph_data, config_data, on='function', how='left')
    involved_method = involved_method.dropna()
    if project_name == 'h2':
        xml_path = '../data/xml/'
        directory = project_path[project_name]
        involved_method['Method_short'] = involved_method['Method'].apply(extract_method_name)
        option_list = involved_method['option'].unique().tolist()
        involved_method['path'] = involved_method['Method'].apply(lambda x: get_method_file_path(directory, x))
        involved_method['class_name'] = involved_method['Method'].apply(lambda x: extract_class_name(x))
        #involved_method.to_csv('../config/' + project_name + '/full_location_tmp.csv', index=False)
        involved_method['xml_path'] = involved_method['path'].apply(lambda x: generate_xml_fle(x, project_name, xml_path))
        involved_method['Method_body'] = involved_method.apply(lambda row: extract_source(row['xml_path'], row['Method_short'], row['class_name'], row['Method']), axis=1)
        involved_method = involved_method[involved_method['option'].isin(option_list)]
        involved_method['update_option'] = involved_method.apply(lambda row: check_and_transform(row['option'],option_list, row['Method_body']), axis=1)
        involved_method = involved_method[involved_method['update_option'] != 'useless']
        involved_method = involved_method.drop(columns = ['Method_short', 'path', 'class_name', 'xml_path', 'option'])
        involved_method.rename(columns={'update_option': 'option'}, inplace=True)
        option_exclude = ['DATABASE_TO_LOWER', 'DEFAULT_ESCAPE']
        involved_method = involved_method[~involved_method['option'].isin(option_exclude)]
        involved_method = involved_method.dropna(subset=['option'])
        involved_method = involved_method[involved_method['option'] != '']
        option_list = involved_method['option'].unique().tolist()
        print(option_list)
        print(len(option_list))
        print('above')
        option_full = pd.read_csv('../data/config_description/h2_description.csv')['Configuration'].tolist()
        difference = list(set(option_full) - set(option_list))
        print(difference)
        print(len(option_list))
    if project_name == 'sunflow':
        xml_path = '../data/xml/'
        directory = project_path[project_name]
        involved_method['Method_short'] = involved_method['Method'].apply(extract_method_name)
        option_list = involved_method['option'].unique().tolist()
        involved_method['path'] = involved_method['Method'].apply(lambda x: get_method_file_path(directory, x))
        involved_method['class_name'] = involved_method['Method'].apply(lambda x: extract_class_name(x))
        #involved_method.to_csv('../config/' + project_name + '/full_location_tmp.csv', index=False)
        involved_method['xml_path'] = involved_method['path'].apply(lambda x: generate_xml_fle(x, project_name, xml_path))
        involved_method['Method_body'] = involved_method.apply(lambda row: extract_source(row['xml_path'], row['Method_short'], row['class_name'], row['Method']), axis=1)
        involved_method['option'] = involved_method.apply(lambda row: get_option_name(row['Method_body']), axis=1)
        #print(len(involved_method['option'].unique().tolist()))
        involved_method['option'] = involved_method['option'].str.split(',')
        involved_method = involved_method.explode('option')
        option_list_check = ['bucket.size', 'threads', 'depths.diffuse', 'depths.reflection', 'depths.refraction',
                            'gi.igi.samples']
        involved_method = involved_method[involved_method['option'].isin(option_list_check)]
        involved_method.drop_duplicates(inplace=True)
        involved_method = involved_method[involved_method['option'] != 'useless']
    if project_name == 'batik':
        xml_path = '../data/xml/'
        directory = project_path[project_name]
        involved_method['Method_short'] = involved_method['Method'].apply(extract_method_name)
        option_list = involved_method['option'].unique().tolist()
        involved_method['path'] = involved_method['Method'].apply(lambda x: get_method_file_path(directory, x))
        involved_method['class_name'] = involved_method['Method'].apply(lambda x: extract_class_name(x))
        involved_method.to_csv('../config/' + project_name + '/full_location_tmp.csv', index=False)
        involved_method = involved_method[involved_method['Jar_name'] == 'batik-all-1.14']
        involved_method = involved_method[involved_method['Method_short'].apply(lambda x: 'getKey' not in x)]
        involved_method['xml_path'] = involved_method['path'].apply(lambda x: generate_xml_fle(x, project_name, xml_path))
        involved_method['Method_body'] = involved_method.apply(lambda row: extract_source(row['xml_path'], row['Method_short'], row['class_name'], row['Method']), axis=1)
        involved_method['option'] = involved_method.apply(lambda row: get_config_batik(row['option'], row['Method_body']), axis=1)
        involved_method['option'] = involved_method['option'].str.split(',')
        involved_method = involved_method.explode('option')
        involved_method = involved_method[involved_method['option'] != 'pageIndex']
        involved_method['option'] = involved_method['option'].apply(parse_option_batik)
        involved_method.drop_duplicates(inplace=True)
        option_list_batik = ["WIDTH", "HEIGHT", "MAX_WIDTH", "MAX_HEIGHT", "AOI", "BACKGROUND_COLOR", "MEDIA",
                             "ALTERNATE_STYLESHEET", "USER_STYLESHEET_URI", "DEFAULT_FONT_FAMILY", "LANGUAGE", "QUALITY",
                             "INDEXED", "PIXEL_UNIT_TO_MILLIMETER", "XML_PARSER_VALIDATING", "EXECUTE_ONLOAD", "SNAPSHOT_TIME",
                             "CONSTRAIN_SCRIPT_ORIGIN", "ALLOWED_SCRIPT_TYPES", "DESTINATION_TYPE", "AREA"]
        involved_method = involved_method[involved_method['option'].isin(option_list_batik)]
        print(len(involved_method['option'].unique().tolist()))



    involved_method.to_csv(f'../config/location/{project_name}_location.csv', index=False)


