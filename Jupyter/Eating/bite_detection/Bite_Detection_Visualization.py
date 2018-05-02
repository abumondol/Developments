
# coding: utf-8

# In[18]:


import numpy as np
import pickle
import os
import sys
import importlib


# In[19]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_classification_utils as mclfu
import my_data_process_utils as mdpu
import my_feature_utils as mfeatu
importlib.reload(mfileu)
importlib.reload(mclfu)
importlib.reload(mdpu)


# In[23]:


hand='left'
if hand == 'right':
    ds = mfileu.read_file('data', 'free_data_steven_right_smoothed.pkl')
else:
    ds = mfileu.read_file('data', 'free_data_steven_left_smoothed.pkl')
    
annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')


# In[ ]:


vth_min, vth_max, xth = 1, 50, 0
win_size = 5*16

vs, gxs = [], []
for subj in range(len(ds)):
    vsubj, gxsubj = [], []
    res = mfileu.read_file('results_meal_free__winsize_160_vth_1_50_xth_0','subj_'+str(subj)+"_"+hand+".pkl") 
    for sess in range(len(ds[subj])):
        print(subj, sess, end=" | ")
        indices = res[sess]["indices"]
        v = mfeatu.get_variance_accel(ds, indices, win_size=win_size)
        gx = mfeatu.get_grav_x(ds, indices, index_offset=win_size//2)
        vsubj.append(v)
        gxsubj.append(gx)
    
    vs.append(vsubj)
    gxs.append(gxsubj)
        


# In[ ]:


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')
for subj in range(11):
    res = mfileu.read_file('results_meal_free__winsize_160_vth_1_50_xth_0','subj_'+str(subj)+"_"+hand+".pkl")    
    for sess in range(len(res)):
        pred = res[sess]["pred"]
        indices = res[sess]["indices"]
        a = annots[subj][sess]
        
        v = vs[subj][sess]
        gx = gxs[subj][sess]
        
        if hand=='right':
            cond = (v>=vth_min)&(v<=vth_max)&(gx<=xth)
        else:
            cond = (v>=vth_min)&(v<=vth_max)&(gx>=xth)
        
        print(cond.shape, np.sum(cond))
        cond = np.logical_not(cond)
        
        pred[cond] = 0
        
        indices = indices[:, 2] + win_size//2
        
        #print(pred.shape)
        #pred = mdpu.simple_moving_average(pred, 30).reshape((-1, 1))
        #print(pred.shape)
        
        
        print(a)
        fig = plt.figure(figsize=(20,5))
        ax = plt.subplot(111)
        ax.scatter(indices/16, pred[:, 0], marker='.', s=1, color='blue')        
        #ax.scatter(indices/16, cond+1.25, marker='.', s=1, color='red')

        clrs = ['', 'blue', 'green', 'red', 'black']
        for i in range(len(a)):
            si = a[i, 0]/16
            ei = a[i, 1]/16
            mt = a[i, 2]    
            ax.plot([si, ei], [1.5, 1.5], color=clrs[mt], linewidth=5)
        plt.axhline(y=2.7)    
        plt.axhline(y=6.3)    
        
        plt.title(str(subj)+","+str(sess))
        plt.ylim([0.8, 2])
        plt.grid(True)
        plt.show()

