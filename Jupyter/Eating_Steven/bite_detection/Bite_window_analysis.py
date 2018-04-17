
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import matplotlib.pyplot as plt


# In[8]:


with open('C:/ASM/DevData/eating_steven/data/lab_windows_steven.pkl', 'rb') as file:
    windows = pickle.load(file)    


# In[30]:


def get_stat(vth):
    #nvs, bvs, svs = np.zeros((0,)), np.zeros((0,)), np.zeros((0,))
    xwcount, nwcount, bwcount, swcount = 0, 0, 0, 0
    xwcount2, nwcount2, bwcount2, swcount2 = 0, 0, 0, 0
    bcov, scov = 0, 0
    bcov2, scov2 = 0, 0    
    for subj in range(7):
        for sess in range(len(windows[subj])):
            w = windows[subj][sess]
            indices, labels, segs  = w["w_indices"], w["labels"], w["segs"]

            #nvs = np.concatenate((nvs, v[l==0]))
            #bvs = np.concatenate((bvs, v[l==1]))
            #svs = np.concatenate((svs, v[l==2])) 

            v = indices[:, -1]
            l = labels[:, 0]
            xwcount += np.sum(l<0)
            nwcount += np.sum(l==0)
            bwcount += np.sum(l==1)
            swcount += np.sum(l==2)
            bcov += len(np.unique(labels[l==1, 2]))
            scov += len(np.unique(labels[l==2, 2]))

            labels = labels[v>=vth, :] 
            l = labels[:, 0]
            xwcount2 += np.sum(l<0)
            nwcount2 += np.sum(l==0)
            bwcount2 += np.sum(l==1)
            swcount2 += np.sum(l==2)
            bcov2 += len(np.unique(labels[l==1, 2]))
            scov2 += len(np.unique(labels[l==2, 2]))

            #print("\nSubj, sess: ", subj, sess)
            #print("nwcount,  bwcount,  swcount,  bcov,   scov: ", nwcount, bwcount, swcount, bcov, scov)
            #print("nwcount2, bwcount2, swcount2, bcov2, scov2: ", nwcount2, bwcount2, swcount2, bcov2, scov2)



    #plt.hist(nvs, bins=100)
    #plt.title("Non bite")
    #plt.show()
    #plt.title("Bite")
    #plt.hist(bvs, bins=100)
    #plt.show()
    #plt.title("Sip")
    #plt.hist(svs, bins=100)
    #plt.show()

    print("xwcount,  nwcount,  bwcount,  swcount,  bcov,   scov: ", xwcount, nwcount, bwcount, swcount, bcov, scov)
    print("xwcount2, nwcount2, bwcount2, swcount2, bcov2, scov2: ", xwcount2, nwcount2, bwcount2, swcount2, bcov2, scov2)

        


# In[31]:


for vth in np.arange(0, 2, 0.1):
    print("vth :", vth)
    get_stat(vth)
    print("")

