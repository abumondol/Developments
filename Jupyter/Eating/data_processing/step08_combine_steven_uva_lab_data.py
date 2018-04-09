
# coding: utf-8

# In[ ]:


import numpy as np
import pickle


# In[ ]:


with open('C:/ASM/DevData/eating/data/steven_lab_data.pkl', 'rb') as file:
    steven_data = pickle.load(file)


# In[ ]:


with open('C:/ASM/DevData/eating/data/uva_lab_data.pkl', 'rb') as file:
    uva_data = pickle.load(file)


# In[ ]:


print(len(steven_data), len(uva_data))


# In[ ]:


combo = steven_data
combo.extend(uva_data)
print(len(combo))


# In[ ]:


with open('C:/ASM/DevData/eating/data/steven_uva_lab_data_combined.pkl', 'wb') as file:
    pickle.dump(combo, file)


# In[ ]:


ds = np.empty((0, 6))
for subject in range(len(combo)):
    for sess in range(len(combo[subject])):
        print(subject, sess, " :: ", end="")
        d = combo[subject][sess]["data"][:, 4:]
        ds = np.concatenate((ds,d), axis=0)
        
print(ds.shape)


# In[ ]:


mu = np.mean(ds, axis=0)
sigma = np.std(ds, axis=0)
mu, sigma


# In[ ]:


for subject in range(len(combo)):
    for sess in range(len(combo[subject])):
        print(subject, sess, " :: ", end="")
        d = combo[subject][sess]["data"][:, 4:]
        combo[subject][sess]["data"][:, 4:] = (d-mu)/sigma        


# In[ ]:


with open('C:/ASM/DevData/eating/data/steven_uva_lab_data_combined_normalized.pkl', 'wb') as file:
    pickle.dump(combo, file)


# In[ ]:


d ={"mu":mu, "sigma":sigma}
with open('C:/ASM/DevData/eating/data/steven_uva_lab_data__normalization_params.pkl', 'wb') as file:
    pickle.dump(d, file)

