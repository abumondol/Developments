
# coding: utf-8

# In[2]:


import numpy as np
import os
import sys


# In[3]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_data_process_utils as mdpu


# In[7]:


dsn = ['lab_data_steven', 'lab_data_uva', 'free_data_steven_right', 'free_data_steven_left']


# In[23]:


ix = 3
ds = mfileu.read_file('data', dsn[ix]+'.pkl')


# In[24]:


for subj in range(len(ds)):
    for sess in range(len(ds[subj])):
        if ix<2:
            ds[subj][sess]["data_right"][:, 1:10] = mdpu.smooth_data(ds[subj][sess]["data_right"][:, 1:10], 0.9)
        else:
            ds[subj][sess][:, 1:10] = mdpu.smooth_data(ds[subj][sess][:, 1:10], 0.9)


# In[25]:


mfileu.write_file('data', dsn[ix]+'_smoothed.pkl', ds)

