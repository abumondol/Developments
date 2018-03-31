
# coding: utf-8

# In[2]:


import numpy as np
import pickle
import os
import matplotlib.pyplot as plt
import data_process_utils as dpu
get_ipython().run_line_magic('matplotlib', 'inline')


# In[3]:


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
    res = res.astype(int)
    return res


# In[4]:


subject, sess = 0, 1
path = 'C:/ASM/PublicData/eating_steventech/lab'
filePathAccel = path + "/0" + str(subject) + "/000" + str(sess) + "/watch_right_000" + str(sess) + ".csv";
filePathAnnots = path + "/0" + str(subject) + "/000" + str(sess) + "/annot_events.csv";

data = np.genfromtxt(filePathAccel, delimiter=',')
data[:, 0] = data[:, 0]/1e9

with open(filePathAnnots) as file:
    annots = file.readlines()

annots = process_bite_annots_steven(annots)
print(data[-1, 0], annots[-1, 0], len(annots))


# In[6]:


_, _, Rz = dpu.quat2mat(data[:, -3:])


# In[ ]:


offset = 60
a = dpu.time_to_index(data, annots[:,0])

for i in range(20):
    ix = a[i]    
    d = data[ix-60:ix+60, :]

    fig = plt.figure(figsize=(14, 7))
    subplot = fig.add_subplot(111)        

    subplot.plot(d[:, 0], d[:, 1], label='AccelX', linestyle='-', color='red')
    subplot.plot(d[:, 0], d[:, 2], label='AccelY', linestyle='-', color='green')
    subplot.plot(d[:, 0], d[:, 3], label='AccelZ', linestyle='-', color='blue')

    #_, _, Rz = dpu.quat2mat(d[:, -3:])
    Rz = Rz*9.8
    subplot.plot(d[:, 0], Rz[:, 0], label='QuatX', linestyle=':', color='red')
    subplot.plot(d[:, 0], Rz[:, 1], label='QuatY', linestyle=':', color='green')
    subplot.plot(d[:, 0], Rz[:, 2], label='QuatZ', linestyle=':', color='blue')

    subplot.legend()            
    plt.title('Subject {}, Sess {}, Annot Index {}'.format(subject, sess, i))            
    plt.show()                        

