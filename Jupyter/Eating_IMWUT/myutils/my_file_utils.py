
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os


# In[2]:


def get_path():
    path = 'C:/ASM/DevData/eating' if 'C:' in os.getcwd() else '.'
    return path


# In[3]:


def create_directory(path):
    if not os.path.exists(path):        
        os.makedirs(path)


# In[4]:


def read_file(folder_name, file_name):
    new_path = get_path()+'/'+folder_name
    with open(new_path+'/'+file_name, 'rb') as file:
        data = pickle.load(file)
    return data


# In[5]:


def write_file(folder_name, file_name, data):
    new_path = get_path()+'/'+folder_name
    create_directory(new_path)
    
    with open(new_path+'/'+file_name, 'wb') as file:
        pickle.dump(data, file)    


# In[6]:


def param_string(params):
    keys = sorted(params.keys())
    s =""
    for key in keys:
        s += key.replace("_", "") + "_" + str(params[key]).replace(".","") + "_"
    
    s = s[:-1]
    return s

