
# coding: utf-8

# In[6]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib


# In[7]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
#importlib.reload(mdu)


# In[8]:


def get_percentile_proba_other_sess(ipvg_subj, exclude_sess, percentile):
    other_p = []
    for sess in range(len(ipvg_subj)):
        if sess == exclude_sess:
            continue
        p = ipvg_subj[sess][:, 1]    
        other_p = p if len(other_p)==0 else np.concatenate((other_p, p))
            
    return np.percentile(other_p, percentile)


# In[9]:


def get_percentile_proba_all(ipvg, lab_free):    
    pct_proba=[]
    for subj in range(len(ipvg)):
        for sess in range(len(ipvg[subj])):
            for p in range(9900, 10000, 5):                
                percentile = p/100
                proba = ipvg[subj][sess][:, 1]
                p_off = np.percentile(proba , percentile)
                p_on = 0 if lab_free=='lab' else get_percentile_proba_other_sess(ipvg[subj], exclude_sess=sess, percentile=percentile)                     
                pct_proba.append([subj, sess, percentile, p_off, p_on])                
                
    return np.array(pct_proba)


# In[10]:


for lab_free in ['lab', 'free']:       
    for clf in ['rf', 'our']:         
        print("\nProcessing pct_proba: ", lab_free, clf)                
        ipvg = mfileu.read_file('ipvg/ipvg_step4', '{}_ipvg_{}.pkl'.format(lab_free, clf))
        pct_proba = get_percentile_proba_all(ipvg, lab_free)
        mfileu.write_file('generated_for_result/pct_proba', '{}_pct_proba_{}.pkl'.format(lab_free, clf), pct_proba)
        
        p = pct_proba
        for i in range(p.shape[0]):
            print("{:.0f}, {:.0f}: {:.2f}, {:.3f}, {:.3f}".format(p[i, 0], p[i, 1], p[i, 2], p[i, 3], p[i, 4]))
        
        print("---------- Done------------")

