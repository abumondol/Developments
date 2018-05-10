
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys
import importlib


# In[4]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_free_utils as msfreeu
import Meal_Detection_Utils as mdu
import my_feature_utils as mfeatu
importlib.reload(mfeatu)


# In[5]:


hand='right'
ds = mfileu.read_file('data', 'free_data_steven_'+hand+'_smoothed.pkl')
annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')


# In[16]:


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')

idf = 16*60*60 #index division factor
win_size = 5*16
for subj in range(11):
    #if subj != 1:
    #    continue
    
    res = mfileu.read_file('results_free/win_10_vth_1_30_xth_0','subj_'+str(subj)+"_"+hand+".pkl") 
    
    pred_subj = res["pred"]
    indices_subj = res["indices"]
    v_subj = res["var"]
    gx_subj= res["gx"]  
    
    for sess in range(len(ds[subj])):        
            
        print('Subj, sess: ', subj, sess)
        a = np.copy(annots[subj][sess])
        dcount = len(ds[subj][sess])
        a = msfreeu.process_anntos(dcount, a)
        
        cond = (indices_subj[:,1]==sess)
        pred = pred_subj[cond, :]
        cix = indices_subj[cond, 4]
        v = v_subj[cond]
        gx = gx_subj[cond]
        
        pred = mdu.filter_by_var_gx(pred, var=v, gx=gx, hand=hand)        
        
        
        c = mdu.find_clusters_free(cix, pred)
        print(c)
        c = c[c[:, 2]>=3, :]
        
        p = mdu.smooth_free(cix, pred)
        p = p[p[:, 1]>=0.5, :]
        p[:, 1] = 2
        

        #pred = mdpu.simple_moving_average(pred, 30).reshape((-1, 1))
        fig = plt.figure(figsize=(20,8))
        ax = plt.subplot(111)
        
        ax.scatter(cix/idf, pred[:, 0], marker='.', s=1, color='blue')                
        ax.scatter(p[:,0]/idf, p[:, 1], marker='o', s=10, color='green')                
        
        '''
        if hand=='right':
            bite_pred = res_bite[sess]["pred"]
            bite_indices = res_bite[sess]["indices"][:, 2]
            bite_var = res_bite[sess]["var"]        
            print(bite_indices.shape, bite_pred.shape, bite_var.shape)
            bite_pred = bite_pred[bite_var>=0.5, :]
            bite_indices = bite_indices[bite_var>=0.5]
            ax.scatter(bite_indices/idf, bite_pred[:, 0]+2, marker='x', s=1, color='green')        
        '''
        
        clrs = ['', 'blue', 'green', 'red', 'black']
        for i in range(len(a)):
            si = a[i, 0]/idf
            ei = a[i, 1]/idf
            mt = a[i, 2]            
            ax.plot([si, ei], [1.5, 1.5], color=clrs[mt], linewidth=5)
                
        for i in range(len(c)):
            si = c[i, 0]/idf
            ei = c[i, 1]/idf            
            ax.plot([si, ei], [2.5, 2.5], color='green', linewidth=5)
                
        #plt.axhline(y=2.7)    
        #plt.axhline(y=6.3)    
        
        ax.set_yticklabels( (0.0, 0.5, 1.0) )
        plt.text(-2.6, 0.5, "Frame probability", fontsize=18)
        plt.text(-2.6, 1.5, "Meals (Ground Truth)", fontsize=18)
        plt.text(-2.6, 2, "Windows selected", fontsize=18)
        plt.text(-2.6, 2.5, "Meals (Detected)", fontsize=18)
        
        for tick in ax.xaxis.get_major_ticks():
            tick.label.set_fontsize(16)
        
        for tick in ax.yaxis.get_major_ticks():
            tick.label.set_fontsize(16)
        
        
        #plt.title(str(subj)+","+str(sess))
        plt.xlabel('Time (Hour)', fontsize=20)
        plt.xlim([-3, 12])
        plt.ylim([0, 3])
        plt.grid(True)
        plt.show()
        
        
        ################################
        fig = plt.figure(figsize=(20,8))
        ax = plt.subplot(111)                  
        clrs = ['', 'blue', 'green', 'red', 'black']
        for i in range(len(a)):
            si = a[i, 0]/idf
            ei = a[i, 1]/idf
            mt = a[i, 2]
            #if mt==1:
            ax.plot([si, ei], [60, 60], color=clrs[mt], linewidth=5)
        
        v = mfeatu.get_variance_for_session(ds[subj][sess], 80, 40)        
        ax.plot(v[:, 0]/idf, v[:, 1] )
        #plt.title("Accelration  Gyro: "+str(subj)+","+str(sess))
        #plt.ylim([0, 100])
        
        for tick in ax.xaxis.get_major_ticks():
            tick.label.set_fontsize(16)
        
        for tick in ax.yaxis.get_major_ticks():
            tick.label.set_fontsize(16)
            
        plt.xlabel('Time (Hour)', fontsize=20)
        plt.ylabel('Total variance in Accelration', fontsize=20)
        
        plt.grid(True)
        plt.show()
        
        ################################
        fig = plt.figure(figsize=(20,8))
        ax = plt.subplot(111)                  
        clrs = ['', 'blue', 'green', 'red', 'black']
        for i in range(len(a)):
            si = a[i, 0]/idf
            ei = a[i, 1]/idf
            mt = a[i, 2]
            if mt==1:
                ax.plot([si, ei], [1.2, 1.2], color=clrs[mt], linewidth=5)
        
        t = ds[subj][sess][:, 0]/(60*60)
        gx = ds[subj][sess][:, -3]
        ax.plot(t, gx)        
        
        for tick in ax.xaxis.get_major_ticks():
            tick.label.set_fontsize(16)
        
        for tick in ax.yaxis.get_major_ticks():
            tick.label.set_fontsize(16)
            
        plt.xlabel('Time (Hour)', fontsize=20)
        plt.ylabel('Gravitaty along $X$ axis', fontsize=20)
        
        plt.grid(True)
        plt.show()
        
        '''
        fig = plt.figure(figsize=(20,8))
        ax = plt.subplot(111)          
        ax.plot(energy[:, 0]//16, energy[:, 2] )        
        plt.title("Energy Linaccel: "+str(subj)+","+str(sess))
        plt.ylim([0, 100])
        plt.grid(True)
        plt.show()
        '''
        

