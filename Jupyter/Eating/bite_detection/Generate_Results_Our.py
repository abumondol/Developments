
# coding: utf-8

# In[1]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_free_utils as msfreeu
import my_data_process_utils as mdpu
import my_meal_detection_utils as mmdu
import my_cluster_utils as mclusteru
#importlib.reload(mdu)


# In[3]:


annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')
free_data = mfileu.read_file('data', 'free_data_steven_right.pkl')


# In[4]:


all_proba_bite = []
for subj in range(len(annots)):
    subj_proba = []    
    for sess in range(len(annots[subj])):
        r = mfileu.read_file('our_test_proba_retrain_personal', 'bite_free_'+str(subj)+'_'+str(sess)+".pkl")
        subj_proba.append(r)        
        
    all_proba_bite.append(subj_proba)    


# In[5]:


def find_events(proba, indices, proba_th):
    count = len(proba)    
    #print("Count total, Threshold, count greater proba_th: ", count, proba_th, np.sum(proba>proba_th))    
    
    res =[]
    inside = False
    for i in range(count):
        if proba[i]>=0.5 and inside==False:
            inside = True
            si = i
        elif proba[i]<0.5 and inside==True:            
            max_proba = np.amax(proba[si:i])            
            res.append([indices[si], indices[i-1], max_proba])
            inside = False
        
    res = np.array(res)
    res = res[res[:, -1]>=proba_th, :2]    
    return res.astype(int)


# In[23]:


if 'C:' in os.getcwd():
    import matplotlib.pyplot as plt
    get_ipython().run_line_magic('matplotlib', 'inline')

def get_result(subj, sess, minCount, mergeDistance, plot=False):
    print_out, sys.stdout = sys.stdout, open(os.devnull, 'w')
    print("Subj, Sess:", subj, sess)
    
    results = []
    
    a = annots[subj][sess]        
    a = msfreeu.process_anntos(len(free_data[subj][sess]), a)
    
    proba = all_proba_bite[subj][sess]
    proba = proba[proba[:, 1]==sess, 2:]
    
    indices = proba[:, 0] + 48
    proba = proba[:, -1]
    proba_th = 0.75 #np.percentile(proba, 99.9)
    bite_indices = find_events(proba, indices, proba_th=proba_th)
    print(bite_indices)
    
    clusters = mclusteru.cluster_bites_by_minute(bite_indices)
    print(clusters)
    clusters = mclusteru.find_meals(clusters, minCount=minCount, mergeDistance=mergeDistance)
    print(clusters)
    
    cond = (clusters[:,1]-clusters[:, 0])>=1*16*60
    clusters = clusters[cond]
    result = mmdu.get_meal_detection_results(a, clusters=clusters)
    
    sys.stdout = print_out
    ###############################################
    if not plot:
        return result

    idf = 16*60*60
    fig = plt.figure(figsize=(20,5))
    ax = plt.subplot(111)        
    ax.scatter(bite_indices[:,0]/idf, np.zeros((len(bite_indices)),)+3, marker='x', s=10, color='blue')                
    
    clrs = ['', 'blue', 'green', 'red', 'black']
    for i in range(len(a)):
        si = a[i, 0]/idf
        ei = a[i, 1]/idf
        mt = a[i, 2]            
        ax.plot([si, ei], [1, 1], color=clrs[mt], linewidth=5)

    for i in range(len(clusters)):
        si = clusters[i, 0]/idf
        ei = clusters[i, 1]/idf            
        ax.plot([si, ei], [2, 2], color='black', linewidth=5)

    plt.title("Subj, Sess: "+str(subj)+", "+str(sess), fontsize=20)
    plt.xlabel('Time (Hour)', fontsize=20)        
    plt.ylim([0, 4])
    plt.grid(True)
    plt.show()        

    return result


# In[24]:


def get_metric_results(minCount, mergeDistance, plotFlag=False):
    gts, acovs, clcovs = [], [], []
    for subj in range(len(annots)):
        for sess in range(len(annots[subj])):
            
            gt, ac, clc = get_result(subj, sess, minCount=minCount, mergeDistance=mergeDistance, plot=plotFlag)
            assert len(gt) == len(ac)

            gt = mdpu.add_subj_sess_to_array(gt, subj, sess, at_begin=False)
            ac = mdpu.add_subj_sess_to_array(ac, subj, sess, at_begin=False)
            clc = mdpu.add_subj_sess_to_array(clc, subj, sess, at_begin=False)

            gts = gt if len(gts)==0 else np.concatenate((gts, gt))
            acovs = ac if len(acovs)==0 else np.concatenate((acovs, ac))
            clcovs = clc if len(clcovs)==0 else np.concatenate((clcovs, clc))
            
    res = mmdu.get_metric_results(gts, acovs, clcovs)
    return res


# In[25]:


res = get_metric_results(minCount=3, mergeDistance=5*60*16)
for key, val in res.items():
    print(key, " : ", val)


# In[ ]:


res ={}
for minMealDistance in range(0, 16, 5): #in minute
    for minMealDuration in range(0, 301, 30): #in second        
        res[(minMealDistance, minMealDuration)] = get_metric_results(minMealDistance = minMealDistance*60*16, minMealDuration=minMealDuration*16)
        print(minMealDistance, minMealDuration, res[(minMealDistance, minMealDuration)]["f1"], end=' | ')
        
mfileu.write_file('final_results', 'our_only_lab.pkl', res)


# In[ ]:


#for key, val in res[(60, 300)].items():
#    print(key, " : ", val)

