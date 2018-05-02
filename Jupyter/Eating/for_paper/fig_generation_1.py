
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys
import importlib


# In[3]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_free_utils as msfreeu
#importlib.reload(mdu)


# In[4]:


hand='right'
if hand == 'right':
    ds = mfileu.read_file('data', 'free_data_steven_right_smoothed.pkl')
else:
    ds = mfileu.read_file('data', 'free_data_steven_left_smoothed.pkl')
    
annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')


# In[16]:


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')


si = 20000
ei = si+16*10
d = ds[0][0][si:ei, :]
t = d[:, 0] - d[0, 0]

for i in range(1, 11, 3):
    fig = plt.figure(figsize=(16,4))
    ax = plt.subplot(111)          
    ax.plot(t, d[:, i], label='X')
    ax.plot(t, d[:, i+1], label='Y')
    ax.plot(t, d[:, i+2], label='Z')
    
    ax.set_yticklabels( () )
    
    for tick in ax.xaxis.get_major_ticks():
        tick.label.set_fontsize(16)
        
    for tick in ax.yaxis.get_major_ticks():
        tick.label.set_fontsize(16)
    

    ax.legend(loc='upper right', fontsize=20)
    plt.xlabel('Time', fontsize=20)
    #plt.ylabel('Value', fontsize=20)
    plt.xlim([0, 10])
    plt.grid(True)    
    plt.show()

