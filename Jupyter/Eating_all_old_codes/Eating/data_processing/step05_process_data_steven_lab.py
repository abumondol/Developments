
# coding: utf-8

# In[5]:


import numpy as np
import pickle
import os
import data_process_utils as dpu


# In[6]:


def process_bite_label_steven(label):
    label = label.replace("P","1").replace("D","2").replace("I","3").replace("Q","4")
    label = label.replace("N","5").replace("C","6").replace("S","7").replace("M","8")
    label = label.replace("R","1").replace("L","2").replace("X","-1")
    return int(label)

def process_bite_annots_steven(annots):    
    a = np.zeros((len(annots), 2))
    for i in range(len(annots)):
        s = annots[i].split(",")
        t1 = float(s[0].rstrip())
        t2 = float(s[1].rstrip())
        label = process_bite_label_steven(s[2].rstrip())
        a[i, 0] = t1+t2/2
        a[i, 1] = label
    
    res = []
    hand = 0    
    last_was_bite = False
    for i in range(len(a)):
        #print(a[i, 0], ", ", a[i, 1])
        if a[i, 1]==21 or a[i,1] ==22:            
            if last_was_bite:    
                if a[i, 1]==21:
                    hand = 1
                else:
                    hand = 2
            else:
                if hand ==1 and a[i, 1]==21 or hand ==2 and a[i, 1]==22:
                    print( "************************ Similar hand found twice ****************")
                
                hand = 3
            
            last_was_bite = False
            
        elif a[i, 1]==3 or a[i, 1]==4:
            if hand==0:
                print("********** Hand is not found : " , annots[i] , ", line no: ", (i + 1))                
                continue
            
            r = [0, 0]
            r[0] = a[i, 0]
            if a[i, 1]==3:    #bite
                r[1] = 1
            else:            #drink
                r[1] = 2
            
            if hand ==1 or hand ==3: #add right hand data only
                res.append(r)
            
            if last_was_bite:
                print("************************ Bite repeated **************** ", i)
            last_was_bite = True        
    
    res = np.array(res)            
    return res


# In[7]:


#read_data_steven_lab

path = 'C:/ASM/PublicData/eating_steventech/lab'
sampling_rate = 16 #Hz
data_all = []

for subject in range(7):
    dsubject = [];
    for sess in range(2):
        if (subject==0 and sess==0) or (subject==1 and sess==1):
            continue

        print("\nSubject, Sess ", subject, sess)
        
        filePathAccel = path + "/0" + str(subject) + "/000" + str(sess) + "/watch_right_000" + str(sess) + ".csv";
        filePathAnnots = path + "/0" + str(subject) + "/000" + str(sess) + "/annot_events.csv";

        data = np.genfromtxt(filePathAccel, delimiter=',')
        t = data[:, 0]/1e9
        t = t.reshape((-1, 1))
        accel_gyro = data[:, 1:7]
        quat = data[:, -3:]
        _ , _ , grav = dpu.quat2mat(quat)

        data = np.concatenate((t, grav, accel_gyro), axis=1)
        data = dpu.resample(data, sampling_rate)
        
        with open(filePathAnnots) as file:            
            annots = file.readlines()
        annots = process_bite_annots_steven(annots)        
        annots[:, 0] = dpu.time_to_index(data, annots[:, 0])
        annots = annots.astype(int)
        
        dsess = {}
        dsess['data'] = data
        dsess['annots'] = annots        
        dsubject.append(dsess)
        
    data_all.append(dsubject)


# In[8]:


with open('C:/ASM/DevData/eating/data/steven_lab_data.pkl', 'wb') as file:
    pickle.dump(data_all, file)

