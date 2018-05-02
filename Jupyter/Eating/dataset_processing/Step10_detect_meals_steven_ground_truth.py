
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
import my_segmentation_utils as msegu
import my_steven_lab_utils as mslabu
importlib.reload(mslabu)
importlib.reload(mdpu)
importlib.reload(msegu)


# In[3]:


ds = mfileu.read_file('data', 'lab_data_steven.pkl')


# In[ ]:


for subj in range(7):
    for sess in range(len(subj)):
        d = ds[subj][sess]
         

