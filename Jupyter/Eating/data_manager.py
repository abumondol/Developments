
# coding: utf-8

# In[1]:

import numpy as np
import pickle
import os


# In[2]:

def get_root_path():
    return "C:\\ASM\\DevData\\eating\\python\\"

def get_slash():
    return "\\"


# In[3]:

def create_folder(folder_name, path=""):
    if len(path)==0:
        path = get_root_path()
        
    subdir_path = path + folder_name        
    directory = os.path.dirname(subdir_path)
    if not os.path.exists(directory):
        os.makedirs(directory)
        print("Directory created: ", directory)
    return subdir_path
    
    


# In[4]:

def get_data(file_name, subdir = ""):    
    if len(subdir)==0:
        file_path = get_root_path() + file_name + ".pkl"
    else:
        subdir_path = create_folder(subdir)         
        file_path = subdir_path + get_slash() + file_name+".pkl"
        
    with open(file_path, "rb") as file:
        res = pickle.load(file)
    
    return res


# In[5]:

def save_data(data, file_name, subdir=""):    
    if len(subdir)==0:
        file_path = get_root_path() + file_name + ".pkl"
    else:
        subdir_path = create_folder(subdir)         
        file_path = subdir_path + get_slash() + file_name+".pkl"
        
    with open(file_path, "wb") as file:
        pickle.dump(data, file)    


# In[6]:

def get_data_uva_lab(smooth_factor=""):
    if len(smooth_factor)==0:
        return get_data("uva_lab_data", "data")
    else:
        return get_data("uva_lab_data_"+smooth_factor, "data")


# In[7]:

def get_data_uva_free(smooth_factor=""):
    if len(smooth_factor)==0:
        return get_data("uva_free_data", "data")
    else:
        return get_data("uva_free_data_"+smooth_factor, "data")


# In[8]:

def get_data_steven_lab(smooth_factor=""):
    if len(smooth_factor)==0:
        return get_data("steven_lab_data", "data")
    else:
        return get_data("steven_lab_data_"+smooth_factor, "data")


# In[4]:

def get_data_steven_free(smooth_factor=""):
    if len(smooth_factor)==0:
        return get_data("steven_free_data", "data")
    else:
        return get_data("steven_free_data_"+smooth_factor, "data")

