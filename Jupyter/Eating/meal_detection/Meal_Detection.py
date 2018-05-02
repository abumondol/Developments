
# coding: utf-8

# In[89]:


import numpy as np
import pickle
import os
import sys
import importlib


# In[90]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_free_utils as msfreeu
import meal_detection_utils as mdu
#importlib.reload(mdu)


# In[91]:


hand='right'

if hand == 'right':
    ds = mfileu.read_file('data', 'free_data_steven_right_smoothed.pkl')
else:
    ds = mfileu.read_file('data', 'free_data_steven_left_smoothed.pkl')
    
annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')


# In[93]:


importlib.reload(mdu)
idf = 16
win_size = 10*16

acovs, clcovs = np.zeros((0, 8)), np.zeros((0, 5))
for subj in range(11):
    res = mfileu.read_file('results_meal_winsize_160_vth_1_50_xth_0_free','subj_'+str(subj)+"_"+hand+".pkl")    
    #res = mfileu.read_file('results_free','subj_'+str(subj)+"_"+hand+".pkl")    
    
    #res_bite = mfileu.read_file('results_bite_detection_free','subj_'+str(subj)+"_"+hand+".pkl")
    #assert len(res_meal)==len(res_bite)    
    
    for sess in range(len(res)):                
        #print('Subj, sess: ', subj, sess)
        a = np.copy(annots[subj][sess])
        dcount = len(ds[subj][sess])
        a = msfreeu.process_anntos(dcount, a)
        
        pred = res[sess]["pred"]
        indices = res[sess]["indices"]
        v = res[sess]["var"]
        gx= res[sess]["gx"]
        
        
        pred = mdu.filter_by_var_gx(pred, var=v, gx=gx, hand=hand)        
        indices = indices[:, 2] + win_size//2
        
        c = mdu.find_clusters_free(indices, pred)
        acov, clcov = mdu.find_meal_result(gt=a, clusters=c, min_count = 3)
        #print(acov)
        #print(clcov)
                
        ssa = np.zeros((len(acov), 8))
        ssa[:, :6] = acov
        ssa[:, 6] = subj
        ssa[:, 7] = sess
        
        
        sscl = np.zeros((len(clcov), 5))        
        sscl[:, :3] = clcov
        sscl[:, 3] = subj
        sscl[:, 4] = sess
        
        acovs = np.concatenate((acovs, ssa), axis=0)
        clcovs = np.concatenate((clcovs, sscl), axis=0)
        
        a[:, :2] = a[:, :2]/idf        
        #print("GT")
        #print(a)
        
        c[:, :2] = c[:, :2]/idf
        #print("Detected")
        #print(c)
        


# In[ ]:


#print(acovs)
#print(clcovs)


# In[51]:


fns = np.sum(clcovs[:, 1]>0)
fnd = np.sum(clcovs[:, 2]>0)
print("fns, fnd: ", fns, fnd)


# In[52]:


total = acovs.shape[0]
tp = np.sum(acovs[:, 0]>=1)
fn = total - tp   #- fns -fnd
fp = np.sum(clcovs[:, 0]==0)

pr = tp/(tp+fp)
rc = tp/(tp+fn)
f1 = 2*pr*rc/(pr+rc)

print("Total, tp, fn, fp: ", total, tp, fn, fp)
print("pr, rc, f1: ", pr, rc, f1)


# In[62]:


fraction_count = np.sum(acovs[:, 0]>1)
fraction_count2 = np.sum(acovs[:, 3]>0)
gaps_cond = acovs[:, 3]>0
gaps = acovs[gaps_cond, 3]/(16*60)
gap_meal_durations = acovs[gaps_cond, 5]/(16*60)
print("Fraction count: ", fraction_count, fraction_count2, gaps, gap_meal_durations)


# In[73]:


cld = np.sum(acovs[:, 4])/(16*60)
gtd = np.sum(acovs[:, 5])/(16*60)
errd = 100*(gtd-cld)/gtd
print("Durations detected, gt, error, rate:", cld, gtd, gtd-cld, errd)

errd = np.sum(np.abs(acovs[:, 5] - acovs[:, 4]))/(16*60)/tp
print("Durations error per meal:", errd)


# In[86]:


se = acovs[:, 1]/(16*60)
ee = acovs[:, 2]/(16*60)

sea = np.abs(se)
eea = np.abs(ee)

print(np.sum((sea>0) & (sea<3)))
print(np.sum((eea>0) & (eea<3)))

import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')

plt.hist(se, bins=100)
plt.show()

plt.hist(ee, bins=100)
plt.show()

