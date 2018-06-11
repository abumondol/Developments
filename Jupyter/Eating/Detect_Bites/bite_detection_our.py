
# coding: utf-8

# In[39]:


import numpy as np
import os 
import sys
import importlib
import copy


# In[40]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu


# In[41]:


ssilp = mfileu.read_file('classification_results', 'free_ssilp_rf_g2.pkl')
ba = mfileu.read_file('data', 'free_data_steven_blank_array.pkl')


# In[42]:


def detect_bites_our(ix, proba, proba_th):
    if np.sum(proba>=proba_th)==0:
        return []
    
    min_distance = 2*16
    count = len(ix)
    res = []
    inside = False
    for i in range(count):
        if proba[i] >= proba_th and inside==False:
            inside = True
            si = i
        elif proba[i]<proba_th and inside==True:            
            ei = i-1            
            mxi = si + np.argmax(proba[si:ei+1])
            res.append([ix[mxi], proba[mxi]])
            inside = False
    
    res = np.array(res).reshape((-1, 2))
    count = res.shape[0]
    flags = np.ones((count, ))
    
    for i in range(count):
        j = i-1
        while j>=0 and res[i, 0]-res[j,0]<min_distance:
            if res[i, 1]<=res[j, 1]:
                flags[i] = 0
                break
            j-=1
                
        if flags[i]==0:
            continue
            
        j = i+1
        while j<count and res[j, 0]-res[i,0]<min_distance:
            if res[i, 1]<res[j, 1]:
                flags[i] = 0
                break
            j+=1

    res = res[flags==1, 0].astype(int)
    return res


# In[43]:


res = {}
for p in range(5, 100, 5):
    proba_th = p/100
    print(proba_th, end=" | ")
    
    ba2 = copy.deepcopy(ba)
    for subj in range(len(ba)):
        for sess in range(len(ba[subj])):                    
            ix = ssilp[subj][sess][:, 2]+40
            proba = ssilp[subj][sess][:, -1]            
            ba2[subj][sess] = detect_bites_our(ix, proba, proba_th)
            
    res[proba_th] = ba2


# In[44]:


mfileu.write_file('bites', 'bites_rf_g2.pkl', res)


# In[45]:


for p in range(5, 100, 5):
    proba_th = p/100
    total = 0
    for subj in range(len(ba)):
        for sess in range(len(ba[subj])):                                
            total += len(res[proba_th][subj][sess])
            #print(res[proba_th][subj][sess])
            
    print(proba_th, total)

