
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys
import data_process_utils as dpu


# In[6]:


def process_annot_line(line):
    line = line.replace("DR","11").replace("DL","12")
    line = line.replace("PR","21").replace("PL","22")    
    line = line.replace("NR","31").replace("NL","32")
    line = line.replace("I","1").replace("Q","2")
    line = line.replace("C","-1").replace("S","-2").replace("M","-3").replace('X', '-100')    
    
    v = line.rstrip().split(',')    
    v = [float(x) for x in v]
    return v


# In[7]:


def process_bite_annots_steven(filepath):
    a = open(filepath).readlines()
    a = [process_annot_line(line) for line in a]
    a = np.array(a)
    
    cond = (a[:, 2]>0) & (a[:, 2]<20)
    original_index = np.arange(len(a))[cond]  
    a = a[cond]
    
    
    acount = len(a)        
    for i in range(acount-1):
        if a[i, 2]==21 and a[i+1, 2]!=22 and a[i+1,2]!=1 and a[i+1,2]!=2:
            print("@@@@@@@@@@@@@@@@@@ Right Sequence Problem at ", i, oi[i])
            
        elif a[i, 2]==22 and a[i+1, 2]!=21 and a[i+1,2]!=1 and a[i+1,2]!=2:    
            print("@@@@@@@@@@@@@@@@@@@@@ Left Sequence Problem at ", i, oi[i])                
    
    i = 0        
    last_was_bite = True
    last_bite_time = 0
    hand = 0
    repeat_count = 0
    
    annots = []        
    for i in range(len(a)):
        if a[i, 2]==11 or a[i, 2]==12:
            if last_was_bite:
                hand = a[i, 2]-10
            else:                
                hand =3                
                if a[i, 0]-a[i-1,0]>1:
                    print("$$$$$$$$$$$$$$$$$$$$$$ Both hand synchronization problem $$$$$$$$$$$$$$$$$$$$$")
                    print("Both hands at index:", original_index[i], ", Time difference: ", a[i, 0]-a[i-1,0])

            last_was_bite = False
            repeat_count = 0

        elif a[i, 2]==1 or a[i, 2]==2:
            r = [a[i,0], a[i,1], a[i,2], hand, 0]
            
            if last_was_bite:
                if hand==0:
                    print("%%%%%%%%%%%%%%%%%%% No hand found**************", i, )
                    r[-1] = -1
                else:
                    repeat_count +=1
                    r[-1] = repeat_count
                    print("*** Repeated at ix:{}, Time:{}, Bite/Sip:{}, hand:{}, interval:{}, repeat_count:{}".format(original_index[i], int(a[i,0]), a[i, 2], hand, int(a[i,0]-last_bite_time),  r[-1]))                    
                
            annots.append(r)                
            last_was_bite = True
            last_bite_time = a[i, 0]
                      
        else:
            print("##### Condition Problem ###########")
            sys.exit(0)
           
    annots = np.array(annots)        
    annots[:,1]= (annots[:,0]+annots[:,1])*16
    annots[:,0]= annots[:,0]*16
    
    return annots.astype(int)    


# In[8]:


def process_data_steven(filepath, sampling_rate = 16, time_problem_index=-1):
    d = np.genfromtxt(filepath, delimiter=',')
    d[:, 0] = d[:, 0]/1e9
    
    if time_problem_index>0:
        ix = time_problem_index
        d[ix:, 0] = d[ix-1, 0] + (d[ix:, 0] - d[ix, 0]) + (d[ix-1, 0]-d[ix-2, 0])        
    
    for i in range(1, len(d)):
        if d[i, 0] - d[i-1, 0]>2:
            print("TTTTTTTTTTTTTTT data time distance is very high, at index:{}, distance:{}".format(i, d[i, 0] - d[i-1, 0]))
    
    time_accel_gyro = d[:, :7]
    quat = d[:, -3:]
    _ , _ , grav = dpu.quat2mat(quat)

    d = np.concatenate((time_accel_gyro, grav), axis=1)
    d = dpu.resample(d, sampling_rate)
    
    return d


# In[9]:


#read_data_steven_lab

path = 'C:/ASM/PublicData/eating_steventech/lab'
sampling_rate = 16 #Hz
data = []

for subj in range(7):
    dsubject=[]
    for sess in range(2):
        if subj==1 and sess==1:
            continue

        print("\n\nSubject, Sess ", subj, sess)
        print("-------------------")
        
        print("====== Right Hand =====")
        filepath = path + "/0" + str(subj) + "/000" + str(sess) + "/watch_right_000" + str(sess) + ".csv"
        if subj==0 and sess==0:
            dr = process_data_steven(filepath, time_problem_index=14827)
        else:
            dr = process_data_steven(filepath)
            
        print("====== Left Hand =====")
        filepath = path + "/0" + str(subj) + "/000" + str(sess) + "/watch_left_000" + str(sess) + ".csv"
        dl = process_data_steven(filepath)
                
        print("====== Annotations =====")
        filepath= path + "/0" + str(subj) + "/000" + str(sess) + "/annot_events.csv";
        a = process_bite_annots_steven(filepath)
        
        dsubject.append({"data_right":dr, "data_left":dl, "annots":a})
        
        print("Data Right Range: {} - {}, sample count, rate: {}, {}".format(dr[0,0], dr[-1,0], len(dr), len(dr)/dr[-1,0]))
        print("Data Left Range : {} - {}, sample count, rate: {}, {}".format(dl[0,0], dl[-1,0], len(dl), len(dl)/dl[-1,0]))        
        print("Annot Range     : {} - {}, Annot count: {}, ".format(a[0,0]/16, a[-1,0]/16, len(a)))
        
        if dr[-1, 0]<a[-1,0]/16:
            print("XXXXXXXXXXXX Annot out of data range right hand")
        #if dl[0,0]>a[0,0] or dl[-1, 0]<a[-1,0]:
        #   print("XXXXXXXXXXXX Annot out of data range left hand")        
        
    data.append(dsubject)
    
with open('C:/ASM/DevData/eating_steven/data/lab_data_steven.pkl', 'wb') as file:
    pickle.dump(data, file)

