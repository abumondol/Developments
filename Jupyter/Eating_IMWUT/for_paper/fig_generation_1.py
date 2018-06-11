
# coding: utf-8

# In[10]:


import numpy as np
import pickle
import os
import sys
import importlib


# In[11]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_free_utils as msfreeu
#importlib.reload(mdu)


# In[12]:


ds = mfileu.read_file('data', 'free_data_steven_right.pkl')   
annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')


# In[30]:


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')


si = 80000
ei = si+16*10
d = ds[0][0][si:ei, :]
t = d[:, 0] - d[0, 0]

k = 1
i = 1
for j in range(si, ei, 8): 
#for i in range(1, 11, 3):
    d = ds[0][0][j:j+32, :]
    t = d[:, 0] - d[0, 0]

    fig = plt.figure(figsize=(4,4))
    ax = plt.subplot(111)          
    
    
    ax.plot(t, d[:, i], label='X', linewidth=3)
    ax.plot(t, d[:, i+1], label='Y', linewidth=3)
    ax.plot(t, d[:, i+2], label='Z', linewidth=3)
    
    ax.set_yticklabels( () )
    ax.set_xticklabels( () )
    
    #for tick in ax.xaxis.get_major_ticks():
    #    tick.label.set_fontsize(16)
        
    #for tick in ax.yaxis.get_major_ticks():
    #    tick.label.set_fontsize(16)
    
    
    #ax.legend(loc='upper right', fontsize=20)
    #plt.xlabel('Time', fontsize=20)
    #plt.ylabel('Value', fontsize=20)
    #plt.xlim([0, 10])
    #plt.grid(True)    
    #plt.show()
    
    plt.savefig('C:/Users/Abu/Desktop/eat_paper_images/solution/for solution section/frames/'+str(k)+'.png', pad_inches=0)
        
    k+=1

