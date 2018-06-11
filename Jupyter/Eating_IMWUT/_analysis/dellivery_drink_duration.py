
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfu
import my_data_process_utils as mdpu


# In[3]:


def process_annot_line(line):
    line = line.replace("DR","11").replace("DL","12")
    line = line.replace("PR","21").replace("PL","22")    
    line = line.replace("NR","31").replace("NL","32")
    line = line.replace("I","1").replace("Q","2")
    line = line.replace("C","-1").replace("S","-2").replace("M","-3").replace('X', '-100')    
    
    v = line.rstrip().split(',')    
    v = [float(x) for x in v]
    return v


# In[4]:


def process_bite_annots(filepath):
    a = open(filepath).readlines()
    a = [process_annot_line(line) for line in a]
    a = np.array(a)
    drink_duration = a[a[:, 2]==2, 1]
    delivery_duration = a[(a[:, 2]==11)|(a[:, 2]==12), 1]
    
    return drink_duration, delivery_duration
    


# In[7]:


#read_data_steven_lab

path = 'C:/ASM/PublicData/eating_steventech/lab'
sampling_rate = 16 #Hz
data = []

drink, delivery = np.empty((0, )), np.empty((0, ))
for subj in range(7):
    dsubject=[]
    for sess in range(2):
        if subj==1 and sess==1:
            continue

                
        filepath= path + "/0" + str(subj) + "/000" + str(sess) + "/annot_events.csv";
        drk, deliv = process_bite_annots(filepath)
        drink = np.concatenate((drink, drk))
        delivery= np.concatenate((delivery, deliv))
        print("Subject, Sess ", subj, sess, ";", np.mean(drk), np.max(drk), ";", np.mean(deliv), np.max(deliv),)
        


# In[11]:


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')
plt.hist(drink, bins=50)
plt.show()
plt.hist(delivery, bins=50)
plt.show()

