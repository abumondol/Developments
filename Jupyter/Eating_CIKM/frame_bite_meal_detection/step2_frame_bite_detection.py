
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


def detect_bites_our(ix_proba, proba_th):
    min_interval = 2*16
    ix, proba = ix_proba[:, 0], ix_proba[:, 1]
    count = len(ix)
    
    peaks = []    
    for i in range(1, count-1):
        if proba[i-1]<proba[i]<=proba[i+1] and proba[i]>=proba_th:        
            peaks.append([ix[i], proba[i]])
            
    peaks = np.array(peaks)    
    if len(peaks)==0:
        return []
    
    cond = (peaks[:,1]>=proba_th)
    peaks = peaks[cond, :]
    ix, proba = peaks[:, 0].astype(int), peaks[:, 1]
            
    while True:
        count = len(ix)
        if count<=1:
            break
            
        flags = np.ones((count, ), dtype=np.int32)        
        if (ix[1] - ix[0]<min_interval) and (proba[0]<proba[1]):
            flags[0] = 0
        
        for i in range(1, count-1):
            cond1 = (ix[i] - ix[i-1]<min_interval) and (proba[i]<=proba[i-1])
            cond2 = (ix[i+1] - ix[i]<min_interval) and (proba[i]<proba[i+1])            
            if cond1 or cond2:
                flags[i] = 0
    
        if (ix[count-1] - ix[count-2]<min_interval) and (proba[count-1]<=proba[count-2]):
            flags[count-1] = 0
    
        if np.sum(flags) == len(flags):
            break
            
        cond = (flags==1)
        ix, proba = ix[cond], proba[cond]
        
    return ix.astype(int)


# In[4]:


def detect_bites_steven(ix_proba, proba_th):
    ix, proba = ix_proba[:, 0], ix_proba[:, 1]
    count = len(ix_proba)    
    
    res = []
    inside = False
    for i in range(count):
        if proba[i] >= proba_th and inside==False:
            inside = True
            si = i
        elif proba[i]<proba_th/2 and inside==True:            
            ei = i-1
            bix = (ix[si]+ix[ei])//2
            res.append(bix)
            inside = False
        
    res = np.array(res).astype(int)
    return res    


# In[5]:


for lab_free in ['lab', 'free']:       
    for clf in ['rf', 'our']:         
        print("\nProcessing frames bites: ", lab_free, clf)

        ipvg = mfileu.read_file('ipvg/ipvg_step4', '{}_ipvg_{}.pkl'.format(lab_free, clf))
        pct_proba = mfileu.read_file('generated_for_result/pct_proba', '{}_pct_proba_{}.pkl'.format(lab_free, clf))
        
        #####################################
        frames_proba, bites_proba = {}, {}
        for subj in range(len(ipvg)):
            for sess in range(len(ipvg[subj])):
                ip = ipvg[subj][sess][:, :2]               
                for p in range(10, 95, 5):            
                    proba_th = p/100
                    #print(proba_th, end=" | ")
                    frames_proba[(subj, sess, proba_th)] = (ip[:, 1]>=proba_th)
                    if clf=='rf':                    
                        bites_proba[(subj, sess, proba_th)] = detect_bites_steven(ip, proba_th)
                    else:
                        bites_proba[(subj, sess, proba_th)] = detect_bites_our(ip, proba_th)
                        
        mfileu.write_file('generated_for_result/frames', '{}_frames_proba_{}.pkl'.format(lab_free, clf), frames_proba)
        mfileu.write_file('generated_for_result/bites', '{}_bites_proba_{}.pkl'.format(lab_free, clf), bites_proba)
        
        #################################
        frames_pct_offline, bites_pct_offline = {}, {}
        frames_pct_online, bites_pct_online = {}, {}                
        pp = pct_proba
        count = len(pp)
        print(count)
        for i in range(count):
            subj, sess, pct, proba_th_offline, proba_th_online = int(pp[i, 0]), int(pp[i, 1]), pp[i, 2], pp[i, 3], pp[i, 4]
            ip = ipvg[subj][sess][:, :2]
            #print(pct, end=" | ")
            
            frames_pct_offline[(subj, sess, pct)] = (ip[:, 1]>=proba_th_offline)
            if clf=='rf':                    
                bites_pct_offline[(subj, sess, pct)] = detect_bites_steven(ip, proba_th_offline)
            else:
                bites_pct_offline[(subj, sess, pct)] = detect_bites_our(ip, proba_th_offline)
                
            if lab_free == 'lab':
                continue
                
            frames_pct_online[(subj, sess, pct)] = (ip[:, 1]>=proba_th_online)
            if clf=='rf':                    
                bites_pct_online[(subj, sess, pct)] = detect_bites_steven(ip, proba_th_online)
            else:
                bites_pct_online[(subj, sess, pct)] = detect_bites_our(ip, proba_th_online)
                

        assert len(bites_pct_offline.keys()) == count
        
        mfileu.write_file('generated_for_result/frames', '{}_frames_pct_offline_{}.pkl'.format(lab_free, clf), frames_pct_offline)
        mfileu.write_file('generated_for_result/bites', '{}_bites_pct_offline_{}.pkl'.format(lab_free, clf), bites_pct_offline)
        if lab_free == 'free':
            mfileu.write_file('generated_for_result/frames', '{}_frames_pct_online_{}.pkl'.format(lab_free, clf), frames_pct_online)
            mfileu.write_file('generated_for_result/bites', '{}_bites_pct_online_{}.pkl'.format(lab_free, clf), bites_pct_online)
        
        print("\n--------------- Done ----------------")

