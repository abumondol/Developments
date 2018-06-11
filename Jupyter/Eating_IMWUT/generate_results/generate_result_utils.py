
# coding: utf-8

# In[3]:


import numpy as np
import os
import sys


# In[4]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_free_utils as msfreeu
import my_cluster_utils as mclusteru
#importlib.reload(mdu)


# In[ ]:


def get_all_proba(annots, folder_path, file_prefix, sess_avail=True):
    all_proba = []
    for subj in range(len(annots)):
        subj_proba = []    
        for sess in range(len(annots[subj])):
            file_path = file_prefix+'_'+str(subj)            
            if sess_avail:
                file_path += '_' + str(sess)                
            file_path += '.pkl'            
            
            r = mfileu.read_file(folder_path, file_path)
            subj_proba.append(r)        

        all_proba.append(subj_proba)    
        
    return all_proba


# In[5]:


def detect_gestures(proba, indices, proba_th):
    count = len(proba)    
    #print("Count total, Threshold, count greater proba_th: ", count, proba_th, np.sum(proba>proba_th))    
    
    res =[]
    inside = False
    for i in range(count):
        if proba[i]>=proba_th and inside==False:
            inside = True
            si = indices[i]
        elif proba[i]<proba_th/2 and inside==True:            
            res.append([si, indices[i-1]])
            inside = False
        
    res = np.array(res).astype(int)
    return res


# In[6]:


def detect_gestures_fixed_th(proba, indices, proba_th):
    count = len(proba)    
    #print("Count total, Threshold, count greater proba_th: ", count, proba_th, np.sum(proba>proba_th))    
    
    res =[]
    inside = False
    for i in range(count):
        if proba[i]>=proba_th and inside==False:
            inside = True
            si = i
        elif proba[i]<proba_th and inside==True:            
            #max_proba = np.amax(proba[si:i])            
            res.append([indices[si], indices[i-1]])
            inside = False
        
    res = np.array(res)
    #res = res[res[:, -1]>=proba_th, :2]    
    return res.astype(int)


# In[7]:


def get_percentile_proba_for_sess(all_proba, fs, subj, sess, percentile):
    p = all_proba[subj][sess][:, -1]    
    extra_count  = len(fs[subj][sess]) - len(p)
    
    if extra_count>0:
        e = np.zeros((extra_count, ))
        p = np.concatenate((p, e))
    
    assert len(fs[subj][sess]) == len(p)    
    return np.percentile(p, percentile)
    


# In[1]:


def get_percentile_proba_other_sess(all_proba, fs, subj, exclude_sess, percentile):
    other_p = []
    for sess in range(len(all_proba[subj])):
        if sess == exclude_sess:
            continue
        p = all_proba[subj][sess][:, -1]    
        extra_count  = len(fs[subj][sess]) - len(p)
    
        if extra_count>0:
            e = np.zeros((extra_count, ))
            p = np.concatenate((p, e))
    
        assert len(fs[subj][sess]) == len(p)    
        
        other_p = p if len(other_p)==0 else np.concatenate((other_p, p))
            
    return np.percentile(other_p, percentile)


# In[2]:


def get_percentile_proba_all(all_proba, fs):
    pct_proba=[]
    for subj in range(len(all_proba)):
        for sess in range(len(all_proba[subj])):
            for percentile in range(9970, 10000):                
                p1 = get_percentile_proba_for_sess(all_proba, fs, subj=subj, sess=sess, percentile=percentile/100)
                p2 = get_percentile_proba_other_sess(all_proba, fs, subj=subj, exclude_sess=sess, percentile=percentile/100)                
                pct_proba.append([subj, sess, percentile/100, p1, p2])
                
    return np.array(pct_proba)


# In[8]:


def plot_result(annots, clusters, bite_indices=[], meal_indices=[], title=""):
    import matplotlib.pyplot as plt
    get_ipython().run_line_magic('matplotlib', 'inline')
    
    idf = 16*60*60
    fig = plt.figure(figsize=(20,5))
    ax = plt.subplot(111)     
    
    if len(bite_indices)>0:
        ax.scatter(bite_indices[:,0]/idf, np.zeros((len(bite_indices)),)+3, marker='x', s=10, color='blue')                
        
    if len(meal_indices)>0:
        ax.scatter(meal_indices[:,0]/idf, np.zeros((len(meal_indices)),)+3, marker='x', s=10, color='green')                
    
    clrs = ['', 'blue', 'green', 'red', 'black']    
    for i in range(len(annots)):
        si = annots[i, 0]/idf
        ei = annots[i, 1]/idf
        mt = annots[i, 2]            
        ax.plot([si, ei], [1, 1], color=clrs[mt], linewidth=5)

    for i in range(len(clusters)):
        si = clusters[i, 0]/idf
        ei = clusters[i, 1]/idf            
        ax.plot([si, ei], [2, 2], color='black', linewidth=5)

    plt.title(title, fontsize=20)
    plt.xlabel('Time (Hour)', fontsize=20)        
    plt.ylim([0, 4])
    plt.grid(True)
    plt.show()

