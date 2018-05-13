
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys
import importlib


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_lab_utils as mslabu
import my_meal_detection_utils as mmdu
#importlib.reload(mdu)


# In[3]:


hand='right'
ds = mfileu.read_file('data', 'free_data_steven_'+hand+'_smoothed.pkl')
annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')


# In[182]:


importlib.reload(mmdu)
idf = 16
win_size = 10*16

tot = 0
acovs, clcovs = np.zeros((0, 8)), np.zeros((0, 5))
for subj in range(11):
    #res = mfileu.read_file('results_meal_winsize_160_vth_1_50_xth_0_free','subj_'+str(subj)+"_"+hand+".pkl")        
    res = mfileu.read_file('results_free','subj_'+str(subj)+"_"+hand+".pkl")    
    #print(res[0].items())
    
    for sess in range(len(res)):                
        #print('Subj, sess: ', subj, sess)
        a = np.copy(annots[subj][sess])
        dcount = len(ds[subj][sess])
        a = msfreeu.process_anntos(dcount, a)
        
        pred = res[sess]["pred"]
        #indices = res[sess]["indices"]
        #v = res[sess]["var"]
        #gx= res[sess]["gx"]
        
        
        #pred = mdu.filter_by_var_gx(pred, var=v, gx=gx, hand=hand)        
        indices = indices[:, 2] + win_size//2
        
        c = mdu.find_clusters_free(indices, pred)        
        #print(c)
        #cond = (c[:, 2]>=3) #& (c[:, 1]-c[:, 0]>=16*60)
        #tot+=np.sum(cond)
        
        
        #print(np.sum(cond))
        
        
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
print(tot)        


# In[174]:


#print(acovs)
#print(clcovs)


# In[175]:


fns = np.sum(clcovs[:, 1]>0)
fnd = np.sum(clcovs[:, 2]>0)
print("fns, fnd: ", fns, fnd)


# In[176]:


total = acovs.shape[0]
tp = np.sum(acovs[:, 0]>=1)
fn = total - tp   #- fns -fnd
fp = np.sum(clcovs[:, 0]==0)

pr = tp/(tp+fp)
rc = tp/(tp+fn)
f1 = 2*pr*rc/(pr+rc)

print("Total, tp, fn, fp: ", total, tp, fn, fp)
print("pr, rc, f1: ", pr, rc, f1)


# In[172]:


fraction_count = np.sum(acovs[:, 0]>1)
fraction_count2 = np.sum(acovs[:, 3]>0)
gaps_cond = acovs[:, 3]>0
gaps = acovs[gaps_cond, 3]/(16*60)
gap_meal_durations = acovs[gaps_cond, 5]/(16*60)
print("Fraction count: ", fraction_count, fraction_count2, gaps, gap_meal_durations)


# In[ ]:


cld = np.sum(acovs[:, 4])/(16*60)
gtd = np.sum(acovs[:, 5])/(16*60)
errd = 100*(gtd-cld)/gtd
print("Durations detected, gt, error, rate:", cld, gtd, gtd-cld, errd)
print(np.sum(acovs[:, 0]==-0))

bcovs = acovs[acovs[:, 0]>0, :]
print(len(bcovs))
#errd = np.sum(np.abs(acovs[:, 5] - acovs[:, 4]))/np.sum(acovs[:, 5])
errd = np.sum(np.abs(bcovs[:, 5] - bcovs[:, 4]))/np.sum(bcovs[:, 5])
print("Durations error per meal:", errd)


# In[ ]:


for subj in range(11):
    b = acovs[acovs[:, -2]==subj, :]
    c = clcovs[clcovs[:, -2]==subj, :]
    total = b.shape[0]
    tp = np.sum(b[:, 0]>=1)
    fn = total - tp   #- fns -fnd
    fp = np.sum(c[:, 0]==0)

    pr = tp/(tp+fp)
    rc = tp/(tp+fn)
    f1 = 2*pr*rc/(pr+rc)

    print(subj)
    print("\tTotal, tp, fn, fp: ", total, tp, fn, fp)
    print("\tpr, rc, f1: ", pr, rc, f1)


# In[ ]:


se = acovs[:, 1]/(16)
ee = acovs[:, 2]/(16)

print(np.sum(np.abs(se))/tp)
print(np.sum(np.abs(ee))/tp)

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

