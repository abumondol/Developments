
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os


# In[ ]:


path = 'C:/ASM/DevData/eating_steven/data' if "C:" in os.getcwd() else 'data'    
with open(path+'/data_steven_lab.pkl', 'rb') as file:
    data = pickle.load(file)

