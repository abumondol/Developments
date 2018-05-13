
# coding: utf-8

# In[14]:


import numpy as np
import os
import sys


# In[15]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu


# In[16]:


res = mfileu.read_file('final_results', 'our_only_lab.pkl')


# In[17]:


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')

def plot_result(metric, ylabel):
    fig = plt.figure(figsize=(10,5))
    ax = plt.subplot(111)  
    
    minMealDurations = list(range(0, 300, 30))        
    for minMealDistance in range(0, 16, 5):
        r = np.zeros((len(minMealDurations), 2))
        for i in range(len(minMealDurations)):                        
            r[i, 0] = minMealDurations[i]
            r[i, 1] = res[(minMealDistance, minMealDurations[i])][metric]
            
        ax.plot(r[:,0], r[:, 1], label= 'MinDist '+str(minMealDistance))
            
    
    plt.title(metric+" (Our)", fontsize=20)
    plt.xlabel("Min Meal Duration", fontsize=20)        
    plt.ylabel(ylabel, fontsize=20)            
    plt.legend()
    plt.grid(True)
    plt.show()       
        


# In[18]:


metrics = ['recall_meal', 'recall_snack', 'recall', 'precision', 'f1', 
           'start_error', 'start_error_meal', 'start_error_snack', 
           'end_error', 'end_error_meal', 'end_error_snack',
           'fragment_error', 'fragment_error_meal', 'fragment_error_snack']

for m in metrics:    
    plot_result(m, " ")

