
# coding: utf-8

# In[1]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
#importlib.reload(mdu)


# In[3]:


def get_percentile_proba_other_sess(all_proba, subj, exclude_sess, percentile):
    other_p = []
    for sess in range(len(all_proba[subj])):
        if sess == exclude_sess:
            continue
        p = all_proba[subj][sess][:, -1]    
        other_p = p if len(other_p)==0 else np.concatenate((other_p, p))
            
    return np.percentile(other_p, percentile)


# In[4]:


def get_percentile_proba_all(all_proba, isLab=False):    
    pct_proba=[]
    for subj in range(len(all_proba)):
        for sess in range(len(all_proba[subj])):
            for p in range(9800, 10000):                
                percentile = p/100
                p_off = np.percentile(all_proba[subj][sess][:, -1] , percentile)
                p_on = 0 if isLab else get_percentile_proba_other_sess(all_proba, subj=subj, exclude_sess=sess, percentile=percentile)                     
                pct_proba.append([subj, sess, percentile, p_off, p_on])                
                
    return np.array(pct_proba)


# In[6]:


step_size = 8

lab_free = 'lab'
assert lab_free in ['lab', 'free']
print("Lab/Free: ", lab_free)

print("\nProcessing ssilv: ")
ssilv = mfileu.read_file('ssilv', '{}_ssilv_steven_right.pkl'.format(lab_free))
for subj in range(len(ssilv)):
    for sess in range(len(ssilv[subj])):
        print(ssilv[subj][sess].shape, end=" | ")
        ssilv[subj][sess] = ssilv[subj][sess][::step_size, :]


res_ap, res_pct = {}, {}
for clf in ['RF', 'our']:         
    print("\n\nProcessing all_proba: ", clf)        
    ap = mfileu.read_file('all_proba', 'all_proba_frame_{}_{}.pkl'.format(lab_free, clf))
    for subj in range(len(ap)):
        for sess in range(len(ap[subj])):
            print(ap[subj][sess].shape, end=" , ")
            ap[subj][sess] = ap[subj][sess][::step_size, :2]
            print(ap[subj][sess].shape, end=" | ")
    res_ap[clf] = ap        

    print("\n\nProcessing pct_proba: ", clf)                
    isLab = True if lab_free=='lab' else False
    pct_proba = get_percentile_proba_all(ap, isLab)
    res_pct[clf] = pct_proba

mfileu.write_file('generated_for_result', 'all_ssilv_{}.pkl'.format(lab_free), ssilv)
mfileu.write_file('generated_for_result', 'all_proba_{}.pkl'.format(lab_free), res_ap)
mfileu.write_file('generated_for_result', 'all_pct_proba_{}.pkl'.format(lab_free), res_pct)
print("Done")

