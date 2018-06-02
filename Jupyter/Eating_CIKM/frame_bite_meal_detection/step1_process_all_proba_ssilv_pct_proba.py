
# coding: utf-8

# In[1]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib


# In[3]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
#importlib.reload(mdu)


# In[ ]:


def get_percentile_proba_for_sess(all_proba, subj, sess, percentile):
    p = all_proba[subj][sess][:, -1]        
    return np.percentile(p, percentile)


# In[ ]:


def get_percentile_proba_other_sess(all_proba, subj, exclude_sess, percentile):
    other_p = []
    for sess in range(len(all_proba[subj])):
        if sess == exclude_sess:
            continue
        p = all_proba[subj][sess][:, -1]    
        other_p = p if len(other_p)==0 else np.concatenate((other_p, p))
            
    return np.percentile(other_p, percentile)


# In[ ]:


def get_percentile_proba_all(all_proba, isLab=False):
    pct_proba=[]
    for subj in range(len(all_proba)):
        for sess in range(len(all_proba[subj])):
            for percentile in range(9800, 10000):                
                p1 = get_percentile_proba_for_sess(all_proba, subj=subj, sess=sess, percentile=percentile/100)
                if isLab:
                    p2 = 0 
                else:
                    p2 = get_percentile_proba_other_sess(all_proba, subj=subj, exclude_sess=sess, percentile=percentile/100) 
                    
                pct_proba.append([subj, sess, percentile/100, p1, p2])                
                
    return np.array(pct_proba)


# In[7]:


step_size = 8
for lab_free in ['lab', 'free']:
    print("-----------------")
    print("Lab/Free: ", lab_free)
    print("-----------------")
    
    print("\nProcessing ssilv: ")
    ssilv = mfileu.read_file('ssilv', '{}_ssilv_steven_right.pkl'.format(lab_free))
    for subj in range(len(ssilv)):
        for sess in range(len(ssilv[subj])):
            print(ssilv[subj][sess].shape, end="| ")
            ssilv[subj][sess] = ssilv[subj][sess][::step_size, :]
    mfileu.write_file('ssilv2', '{}_ssilv_steven_right.pkl'.format(lab_free), ssilv)    
    
    for rf_our in ['RF', 'our']: 
        print("\nProcessing all_proba: ", rf_our)        
        p = mfileu.read_file('all_proba', 'all_proba_frame_{}_{}.pkl'.format(lab_free, rf_our))
        for subj in range(len(p)):
            for sess in range(len(p[subj])):
                print(p[subj][sess].shape, end="| ")
                p[subj][sess] = p[subj][sess][::step_size, :2]
        mfileu.write_file('all_proba2', 'all_proba_frame_{}_{}.pkl'.format(lab_free, rf_our), p)
        
        print("\nProcessing pct_proba: ", rf_our)                
        isLab = True if lab_free=='lab' else False
        pct_proba = get_percentile_proba_all(p, isLab)
        mfileu.write_file('pct_proba2', 'pct_proba_{}_{}.pkl'.format(lab_free, rf_our), pct_proba)

