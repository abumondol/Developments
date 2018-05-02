
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
import my_classification_utils as mclfu
import my_data_process_utils as mdpu
import my_feature_utils as mfeatu
import my_steven_lab_utils as mslabu
#importlib.reload(mfileu)


# In[3]:


hand='right'
dsa = mfileu.read_file('data', 'lab_data_steven.pkl')
ds_right, ds_left, annots = mslabu.separate_right_left_annots(dsa)
annots = mslabu.adjust_annots_all(annots)
ds = ds_right  if hand=='right' else ds_left    
annots = mslabu.filter_annots_all(annots, hand=hand)


# In[4]:


vth_min, vth_max, xth = 1, 50, 0
win_size = 5*16


# In[6]:


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')
for subj in range(7):
    #res_meal = mfileu.read_file('results_meal_winsize_160_vth_1_50_xth_0_lab','subj_'+str(subj)+"_"+hand+".pkl")    
    res_bite = mfileu.read_file('results_bite_detection_lab','subj_'+str(subj)+"_"+hand+".pkl")
    #print('Len res_meal, res_bite:', len(res), len(res_bite))
    
    for sess in range(len(res_bite)):
        print(subj, sess)        
        print(len(res_bite[sess]))
        #if True: continue
        a = annots[subj][sess]
        '''
        pred = res_meal[sess]["pred"]
        indices = res_meal[sess]["indices"]
        v = res_meal[sess]["var"]
        gx= res_meal[sess]["gx"]
        
        
        if hand=='right':
            cond = (v>=vth_min)&(v<=vth_max)&(gx<=xth)
        else:
            cond = (v>=vth_min)&(v<=vth_max)&(gx>=xth)
        
        print(cond.shape, np.sum(cond))
        cond = np.logical_not(cond)        
        pred[cond] = 0
        indices = indices[:, 2] + win_size//2
        '''
        
        bite_pred = res_bite[sess]["pred"]
        bite_indices = res_bite[sess]["indices"][:, 2]
        bite_var = res_bite[sess]["var"]        
        print(bite_indices.shape, bite_pred.shape, bite_var.shape)
        bite_pred = bite_pred[bite_var>=0.5, :]
        bite_indices = bite_indices[bite_var>=0.5]
        
        
        
        #print(pred.shape)
        #pred = mdpu.simple_moving_average(pred, 30).reshape((-1, 1))
        #print(pred.shape)
        
        fig = plt.figure(figsize=(20,8))
        ax = plt.subplot(111)
        #ax.scatter(indices/16, pred[:, 0], marker='.', s=1, color='blue')        
        ax.scatter(bite_indices/16, bite_pred[:, 0]+2, marker='.', s=2, color='green')        
        
        
        #ax.scatter(indices/16, cond+1.25, marker='.', s=1, color='red')
        #print(a)
        clrs = ['', 'blue', 'green', 'red', 'black']
        ax.scatter(a[:, 0]/16, np.zeros((len(a),))+1.5 , marker='.')
        #plt.axhline(y=2.7)    
        #plt.axhline(y=6.3)    
        
        plt.title(str(subj)+","+str(sess))
        plt.ylim([0, 3])
        plt.grid(True)
        plt.show()

