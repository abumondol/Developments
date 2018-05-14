
# coding: utf-8

# In[1]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib
import generate_result_utils as gresu


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_free_utils as msfreeu
import my_cluster_utils as mclusteru
#importlib.reload(mdu)


# In[3]:


annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')
ds = mfileu.read_file('data', 'free_data_steven_right.pkl')
fs = mfileu.read_file('features', 'free_features_steven_right.pkl')


# In[4]:


all_proba_steven = gresu.get_all_proba(annots, 'baseline_test_proba_bite_steven', 'bite_free', sess_avail=True)
pct_proba_steven = gresu.get_percentile_proba_all(all_proba_steven, fs)


# In[5]:


all_proba_our = gresu.get_all_proba(annots, 'our_test_proba_bite_6s', 'bite_free', sess_avail=True)
pct_proba_our = gresu.get_percentile_proba_all(all_proba_our, fs)


# In[6]:


def get_bites(subj, sess, percentile, isSteven):
    if isSteven:
        proba = all_proba_steven[subj][sess]
        indices = proba[:, 0] + 40        
        pct_proba = pct_proba_steven
    else:
        proba = all_proba_our[subj][sess]
        indices = proba[:, -2] + 48
        pct_proba = pct_proba_our
        
    proba = proba[:, -1]
    
    cond  = (pct_proba[:, 0]==subj) & (pct_proba[:, 1]==sess) & (pct_proba[:, 2]==percentile)
    assert np.sum(cond)==1    
    proba_th_offline, proba_th_online = pct_proba[cond, -2], pct_proba[cond, -1]    
    
    bites_offline = gresu.detect_gestures(proba, indices=indices, proba_th=proba_th_offline)
    bites_online = gresu.detect_gestures(proba, indices=indices, proba_th=proba_th_online)
    
    return bites_offline, bites_online


# In[7]:


def get_bites_for_percentile(percentile, isSteven):    
    res ={}    
    for subj in range(len(annots)):
        for sess in range(len(annots[subj])):            
            bites_offline, bites_online = get_bites(subj, sess, percentile, isSteven)
            res[(subj, sess)] = {"offline":bites_offline, "online":bites_online}            
    return res


# In[8]:


res ={}
isSteven = True 
for p in range(9980, 10000):
    percentile = p/100    
    print(percentile, isSteven)
    res[percentile] = get_bites_for_percentile(percentile, isSteven)
    
mfileu.write_file('final_results', 'all_bites_steven.pkl', res)

