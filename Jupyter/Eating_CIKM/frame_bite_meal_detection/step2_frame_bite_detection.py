
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


# In[4]:


def detect_bites(ix_proba, proba_th):
    min_interval = 2*16
    
    count = len(ix_proba)
    ix = ix_proba[:, 0]
    proba = ix_proba[:, 1]    
    
    peaks = []    
    for i in range(1, count-1):
        if proba[i-1]<proba[i]<=proba[i+1] and proba[i]>=proba_th:        
            peaks.append(i)
    #print(len(peaks))
    
    if len(peaks)==0:
        return []
    elif len(peaks)==1:
        return np.array([ix[peaks[0]]])
    
    while True:
        count = len(peaks)
        if count<=1:
            break
        
        bites = []
        
        if proba[peaks[0]]>proba[peaks[1]] or ix[peaks[1]] - ix[peaks[0]]>=min_interval:
            bites.append(peaks[0])            
        
        for i in range(1, count-1):
            cond1 = (proba[peaks[i]]>proba[peaks[i+1]]) and proba[peaks[i]]>proba[peaks[i-1]]
            cond2 = (ix[peaks[i+1]] - ix[peaks[i]]>=2*16) and (ix[peaks[i]] - ix[peaks[i-1]]>=min_interval)
            if cond1 or cond2:
                bites.append(peaks[i])
    
        if proba[peaks[count-1]]>proba[peaks[count-2]] or ix[peaks[count-1]] - ix[peaks[count-2]]>=min_interval:
            bites.append(peaks[count-1])
    
        if len(bites)==len(peaks):
            break
            
        peaks = bites
    
    
    if len(peaks)==0:
        return []
    
    indices = [ix[i] for i in peaks]
    return np.array(indices).astype(int)


# In[5]:


def get_frames_bites(ix_proba, percentile_proba, percentile_proba_val, pct_proba=None, off_on=None, blank_array = []):
    assert off_on in [None, "offline", "online"]      
    assert percentile_proba in ["percentile", "proba"]    
    
    ba = blank_array
    frames = copy.deepcopy(blank_array)
    bites = copy.deepcopy(blank_array)
    
    for subj in range(len(ba)):
        for sess in range(len(ba[subj])):            
            ix_p = ix_proba[subj][sess][:, :2]
            ix_p[:, 0] = ix_p[:, 0]+40 #add offset
            
            if percentile_proba=='percentile':                
                cond  = (pct_proba[:, 0]==subj) & (pct_proba[:, 1]==sess) & (pct_proba[:, 2]==percentile_proba_val)
                assert np.sum(cond)==1                        
                proba_th = pct_proba[cond, -2] if off_on=="offline" else pct_proba[cond, -1]
            else:
                proba_th = percentile_proba_val
                        
            frames[subj][sess] = ix_p[ix_p[:, 1]>=proba_th, 0]            
            bites[subj][sess] = detect_bites(ix_p, proba_th=proba_th)                            
            
    assert len(frames)>=len(bites)        
    return frames, bites


# In[6]:


def get_frames_bites_all(lab_free, clf):
    if lab_free =='lab':
        ba = mfileu.read_file('data', 'lab_data_steven_blank_array.pkl')
    else:
        ba = mfileu.read_file('data', 'free_data_steven_blank_array.pkl')
    
    ix_proba = all_proba[lab_free][clf]
    pct_proba = all_pct_proba[lab_free][clf]
    
    frames = {"proba":{}, "percentile_offline":{}, "percentile_online":{} }
    bites = {"proba":{}, "percentile_offline":{}, "percentile_online":{} }    
        
    for p in range(10, 95, 5):            
        proba = p/100
        print(proba, end=" | ")
        f, b = get_frames_bites(ix_proba, "proba", percentile_proba_val=proba, blank_array=ba)             
        frames["proba"][proba] = f   
        bites["proba"][proba] = b

    for p in range(9800, 10000):
        percentile = p/100                            
        print(percentile, end=" | ")

        f, b = get_frames_bites(ix_proba, "percentile", percentile_proba_val=percentile, pct_proba=pct_proba, off_on="offline", blank_array=ba)
        frames["percentile_offline"][percentile] = f
        bites["percentile_offline"][percentile] = b

        if lab_free == 'free':
            f, b = get_frames_bites(ix_proba, "percentile", percentile_proba_val=percentile, pct_proba=pct_proba, off_on="online", blank_array=ba)
            frames["percentile_online"][percentile] = f
            bites["percentile_online"][percentile] = b
    
    return frames, bites
   


# In[7]:


#res_lab_free = mfileu.read_file('generated_for_result', 'all_frames_bites.pkl')
all_frames, all_bites = {}, {}

for lab_free in ['lab', 'free']:    
    frames_clf, bites_clf = {}, {}
    for clf in ['RF', 'our']:        
        print("\n\n--------------", lab_free, clf, "--------------")
        f, b = get_frames_bites_all(lab_free, clf)
        frames_clf[clf] = f
        bites_clf[clf] = b
        
    all_frames[lab_free] = frames_clf
    all_bites[lab_free] = bites_clf
            
mfileu.write_file('generated_for_result', 'all_frames.pkl', all_frames)
mfileu.write_file('generated_for_result', 'all_bites.pkl', all_bites)
print("Done!!!")

