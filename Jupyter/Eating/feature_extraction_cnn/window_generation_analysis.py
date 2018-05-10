
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys
import importlib


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_data_process_utils as mdpu


# In[17]:


ds = mfileu.read_file("data", "free_data_steven_right.pkl")
annots = mfileu.read_file("data", "free_data_steven_annots.pkl")


# In[18]:


print(annots[0][0].shape)


# In[24]:


meal_type_count =[0, 0, 0, 0, 0]
whole_out =[0, 0, 0, 0, 0]
partial_out =[0, 0, 0, 0, 0]
durations = [0, 0, 0, 0, 0]
total_data_duration = 0

for subj in range(len(ds)):
    for sess in range(len(ds[subj])):
        print("\nSubj, sess: ", subj, sess)
        d = ds[subj][sess]
        a = annots[subj][sess]
        print("Shapes: ", d.shape, a.shape)
        
        dt = int(len(d)/16)
        total_data_duration+=dt
        print("Data Length: ", dt)
        print("Meals (Start time, End time, Type): total ",len(a))
        
        for i in range(len(a)):
            st, et, meal_type = int(a[i,0]/16), int(a[i,1]/16), int(a[i, 2])
            meal_type_count[meal_type] += 1
            print("  ", st, et, meal_type, " : ", et-st+1)
            
            if st>dt:
                print("       ********* Whole out xxxxxxxxxxxx")
                whole_out[meal_type]+=1
            elif et>dt:
                print("       xxxxxxxxxxxx Partial out xxxxxxxxx")
                partial_out[meal_type]+=1
                durations[meal_type]+=(dt-st+1)
            else:
                durations[meal_type]+=(et-st+1)


# In[23]:


print(meal_type_count, " : ", sum(meal_type_count))
print(whole_out, " : ", sum(whole_out))
print(partial_out, " : ", sum(partial_out))
print(total_data_duration, total_data_duration-)
print(durations, " : ", durations[0]+durations[1])

