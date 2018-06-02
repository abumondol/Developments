
# coding: utf-8

# In[1]:


import numpy as np
import os 
import sys
import importlib
import copy


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu


# In[3]:


all_proba = mfileu.read_file('generated_for_result', 'all_proba.pkl')
all_pct_proba = mfileu.read_file('generated_for_result', 'all_pct_proba.pkl')


# In[5]:


def get_framess(ix_proba, proba_th, blank_array = []):    
    ba = blank_array
    frames = copy.deepcopy(blank_array)
    bites = copy.deepcopy(blank_array)
    
    for subj in range(len(ba)):
        for sess in range(len(ba[subj])):            
            ix_p = ix_proba[subj][sess][:, :2]
            ix_p[:, 0] = ix_p[:, 0]+40 #add offset                        
            frames[subj][sess] = ix_p[ix_p[:, 1]>=proba_th, 0]                        
            
    return frames


# In[6]:


def get_frames_all(lab_free, RF_our):
    if lab_free =='lab':
        ba = mfileu.read_file('data', 'lab_data_steven_blank_array.pkl')
    else:
        ba = mfileu.read_file('data', 'free_data_steven_blank_array.pkl')
    
    ix_proba = all_proba[lab_free][RF_our]
    pct_proba = all_pct_proba[lab_free][RF_our]
    
    x = {"proba_frames":{}, "percentile_frames_offline":{}, "percentile_frames_online":{}}
        
    for p in range(10, 95, 5):            
        proba = p/100
        print(proba, end=" | ")
        x["proba_frames"][proba] = get_frames(ix_proba, proba, blank_array=ba)                      
    
    for p in range(9800, 10000):
        percentile = p/100                            
        print(percentile, end=" | ")

        frames, bites = 
        x["percentile_frames_offline"][percentile] = get_frames_bites(ix_proba, "percentile", percentile_proba_val=percentile, pct_proba=pct_proba, off_on="offline", blank_array=ba)
        x["percentile_bites_offline"][percentile] = bites

        if lab_free == 'free':
            frames, bites = get_frames_bites(ix_proba, "percentile", percentile_proba_val=percentile, pct_proba=pct_proba, off_on="online", blank_array=ba)
            x["percentile_frames_online"][percentile] = frames
            x["percentile_bites_online"][percentile] = bites
    
    return x
   


# In[7]:


#res_lab_free = mfileu.read_file('generated_for_result', 'all_frames_bites.pkl')
res_lab_free = {}

for lab_free in ['lab', 'free']:    
    res_RF_our = {}    
    for RF_our in ['RF', 'our']:        
        print("\n\n--------------", lab_free, RF_our, "--------------")
        res_RF_our[RF_our] = get_frames_bites_all(lab_free, RF_our)
        
    res_lab_free[lab_free] = res_RF_our
            
mfileu.write_file('generated_for_result', 'all_frames_bites.pkl', res_lab_free)
print("Done!!!")

