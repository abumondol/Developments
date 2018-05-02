
# coding: utf-8

# In[2]:


import numpy as np
import pickle
import os

import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')


# In[37]:


def process_annots_uva(annots, accel):
    #annots[:, 0] = time_to_index(accel, annots[:, 0])
    annot_count = len(annots)
    flags = np.ones((annot_count,))
    for i in range(annot_count):
        if 1 <= annots[i, 1] < 400:
            annots[i, 1] = 1
        elif 400 <= annots[i, 1] < 1000:
            annots[i, 1] = 2
        else:
            print("XXXXXXX UVA annot Problem XXXXXXXXXXX")
            
        if i>=1 and annots[i,0] - annots[i-1, 0]<=1:
            flags[i] = 0 
    
    #print(np.sum(flags))
    annots = annots[flags==1, :]
    #annots = annots.astype(float)
    return annots
    

def read_data_uva_lab():
    print("Reading data UVA lab")
    path = 'C:\\ASM\\DevData\\eating\\our_data\\lab_data\\'
    data = []
    for i in range(35):        
        accel = np.genfromtxt(path + "accel_"+str(i), delimiter=',')
        #print(accel.shape)
        #print(accel[:10, :])
        print("\nSession ", str(i), " Before process >> Duration: ", str(accel[0, 0]), " - ", str(accel[-1, 0]), ", Count: "+str(len(accel)) )         
        
        
        #accel = resample(accel, sampling_rate)
        #print(accel[:10, :])
        #accel[:, 1:] = smooth_data(accel[:, 1:], smooth_factor)
        #print("Session ", str(i), " After process >> Duration: ", str(accel[0, 0]), " - ", str(accel[-1, 0]), ", Count: "+str(len(accel)) )         
        #print(accel[:10, :])
          
        annots = np.genfromtxt(path+"annot_"+str(i), delimiter=',') 
        annots = process_annots_uva(annots, accel)
        print("Session ", str(i), " Annots >> Duration: ", str(annots[0, 0]), " - ", str(annots[-1, 0]), ", Count: "+str(len(annots)) )         
                
        #print(annots[:10, :])
        
        dsess = [accel, annots]        
        dsubject = [dsess]        
        data.append(dsubject)
        
    
    #dm.save_data(data, "uva_lab_data", "data")            
    return data
    


# In[38]:


d = read_data_uva_lab()


# In[46]:


durations = []
annots_all = np.empty((0, 2))
all_diff = []
for subject in range(len(d)):
    subject_data = d[subject]
    for sess in range(len(subject_data)):
        accel = d[subject][sess][0]
        durations.append([accel[-1,0]])
        annots = d[subject][sess][1]
        annots_all= np.concatenate((annots_all, annots))        
        a = annots[:, 0]
        diff = np.diff(a)
        all_diff = np.concatenate((all_diff, diff))
        
print(durations)
print(np.mean(durations)/60)
a = annots_all        
print("Total annots:", len(a), len(a[a[:,1]==1, :]), len(a[a[:,1]==2, :]))

all_diff = all_diff[all_diff<=10]

fig, ax = plt.subplots()
plt.hist(all_diff, bins=50)
plt.xlim([0, 10])
plt.xlabel('Interval between consecutive bites/sips (Seconds)')
plt.ylabel('Count')
        

