
# coding: utf-8

# In[16]:


import numpy as np
import scipy as sp
import os 
import sys
import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')


# In[17]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu


# In[18]:


annots = mfileu.read_file('data', 'free_annots_steven_processed.pkl')
ssilp = mfileu.read_file('classification_results', 'free_ssilp_rf_g2.pkl')


# In[19]:


def plot_fig(a, ilp, ttl):    
    
    fig = plt.figure(figsize=(16, 5))
    ax = plt.subplot(111)  
    
    c = ['y', 'b', 'g', 'r']
    
    for i in range(len(a)):
        si, ei, label = a[i, 0]/16/60, a[i, 1]/16/60, a[i, 2]
        print("{:5.2f}, {:5.2f}, {:4.2f}, {:d}".format(si, ei, ei-si, label))        
        ax.plot([si, ei], [1.5, 1.5], linewidth=5, color=c[label])
       
    
    for label in range(1, 4):
        cond = (ilp[:,1]==label)
        x = ilp[cond, 0]/16/60
        y = np.zeros((len(x), )) + 1.25                
        ax.scatter(x, y, color=c[label],  marker='.', s=1)        
    
    
    x = ilp[:, 0]/16/60
    y = ilp[:, 2]
    ax.scatter(x, y, color='b', marker='.', s=1)   
       
        
    plt.title(ttl)
    plt.xlim([0, 825])
    plt.ylim([0, 1.6])
    plt.grid(True)
    plt.show()  


# In[20]:


for subj in range(len(annots)):
    for sess in range(len(annots[subj])):       
        ilp = ssilp[subj][sess][:, 2:]        
        assert np.sum ((ilp[1:, 0] - ilp[:-1, 0])<=0) == 0   
        
        
        ttl = "Subj, Sess: {}, {}".format(subj, sess)
        plot_fig(annots[subj][sess], ilp, ttl)
        
        

