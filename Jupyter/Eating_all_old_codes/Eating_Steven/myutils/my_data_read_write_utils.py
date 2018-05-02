
# coding: utf-8

# In[14]:


import numpy as np
import pickle
import os
import sys


# In[15]:


path = 'C:/ASM/DevData/eating_steven' if 'C:' in os.getcwd() else '.'


# In[16]:


def read_file(folder_name, file_name):
    new_path = path+'/'+folder_name
    with open(new_path+'/'+file_name, 'rb') as file:
        data = pickle.load(file)
    return data


# In[17]:


def write_file(folder_name, file_name, data):
    new_path = path+'/'+folder_name
    if not os.path.exists(new_path):
        os.makedirs(new_path)
    
    with open(new_path+'/'+file_name, 'wb') as file:
        pickle.dump(data, file)
    

