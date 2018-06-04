
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


# In[13]:


lab_free = 'lab'
assert lab_free in ['lab', 'free']
print("Lab/Free: ", lab_free)

all_proba = mfileu.read_file('generated_for_result', 'all_proba_{}.pkl'.format(lab_free))
all_pct_proba = mfileu.read_file('generated_for_result', 'all_pct_proba_{}.pkl'.format(lab_free))
blank_array = mfileu.read_file('data', '{}_data_steven_blank_array.pkl'.format(lab_free))


# In[5]:


def detect_bites(ix_proba, proba_th):
    min_interval = 1*16
    
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


# In[17]:


def get_frames_bites(ix_proba, percentile_proba, percentile_proba_val, pct_proba=None, off_on=None):
    assert off_on in [None, "offline", "online"]      
    assert percentile_proba in ["percentile", "proba"]    
    
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
            
            print(subj, sess, proba_th)
                        
            #frames[subj][sess] = ix_p[ix_p[:, 1]>=proba_th, :]            
            bites[subj][sess] = detect_bites(ix_p, proba_th=proba_th)                            
            
    assert len(frames)>=len(bites)        
    return frames, bites


# In[18]:


def get_frames_bites_all(clf):    
    
    ix_proba = all_proba[clf]
    pct_proba = all_pct_proba[clf]
    
    frames = {"proba":{}, "percentile_offline":{}, "percentile_online":{} }
    bites = {"proba":{}, "percentile_offline":{}, "percentile_online":{} }    
        
    for p in range(10, 95, 5):            
        proba = p/100
        print(proba, end=" | ")
        #f, b = get_frames_bites(ix_proba, "proba", percentile_proba_val=proba, blank_array=ba)             
        #frames["proba"][proba] = f   
        #bites["proba"][proba] = b

    for p in range(9900, 10000, 10):
        percentile = p/100                            
        print(percentile, end=" | ")

        f, b = get_frames_bites(ix_proba, "percentile", percentile_proba_val=percentile, pct_proba=pct_proba, off_on="offline")
        frames["percentile_offline"][percentile] = f
        bites["percentile_offline"][percentile] = b

        if lab_free == 'free':
            f, b = get_frames_bites(ix_proba, "percentile", percentile_proba_val=percentile, pct_proba=pct_proba, off_on="online")
            frames["percentile_online"][percentile] = f
            bites["percentile_online"][percentile] = b
    
    return frames, bites
   


# In[19]:


frames, bites = {}, {}
for clf in ['RF', 'our']:        
    print("\n\n--------------", lab_free, clf, "--------------")
    f, b = get_frames_bites_all(clf)
    frames[clf] = f
    bites[clf] = b
            
mfileu.write_file('generated_for_result', 'all_frames_{}.pkl'.format(lab_free), frames)
mfileu.write_file('generated_for_result', 'all_bites_{}.pkl'.format(lab_free), bites)
print("Done!!!")

