
# coding: utf-8

# In[1]:


import numpy as np
import pickle


# In[2]:


with open('C:/ASM/DevData/eating/data/steven_lab_data.pkl', 'rb') as file:
    steven_data = pickle.load(file)


# In[3]:


with open('C:/ASM/DevData/eating/data/uva_lab_data.pkl', 'rb') as file:
    uva_data = pickle.load(file)


# In[4]:


print(len(steven_data), len(uva_data))


# In[5]:


combo = steven_data
combo.extend(uva_data)
print(len(combo))


# In[6]:


with open('C:/ASM/DevData/eating/data/steven_uva_lab_data_combined.pkl', 'wb') as file:
    pickle.dump(combo, file)

