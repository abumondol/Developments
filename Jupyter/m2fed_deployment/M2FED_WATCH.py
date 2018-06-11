
# coding: utf-8

# In[ ]:


import numpy as np
import time
import os
import sys
import traceback
from datetime import datetime
import time
from keras.models import Sequential, load_model


# In[ ]:


def create_directory(path):
    directory = os.path.dirname(path)
    if not os.path.exists(directory):
        os.makedirs(directory)
        print("Directory created: ", directory)


# In[ ]:


def milli_to_str(t):
    t = int(t/1000)
    s = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(t))
    return s
    


# In[ ]:


source_folder = "C:/xampp/htdocs/m2fed_watch/uploads/"
dest_folder_root = "M2FED_Results/"

dest_folders = {}
dest_folders["rawdata"] = dest_folder_root+"RawData/M2FED_Watch_Data/"
dest_folders["rawdata_error"] = dest_folder_root+"RawData/M2FED_Watch_Data/error_files/"
dest_folders["battery"] = dest_folder_root+"Eating/battery/"
dest_folders["beacon"] = dest_folder_root+"Eating/beacon/"
dest_folders["bite"] = dest_folder_root+"Eating/bite/"
dest_folders["ema"] = dest_folder_root+"Eating/ema/"
dest_folders["location"] = dest_folder_root+"Eating/location/"
dest_folders["log"] = dest_folder_root+"Eating/log/"
dest_folders["meal"] = dest_folder_root+"Eating/meal/"

dest_folders["battery_readable"] = dest_folder_root+"Eating_readable/battery/"
dest_folders["beacon_readable"] = dest_folder_root+"Eating_readable/beacon/"
dest_folders["bite_readable"] = dest_folder_root+"Eating_readable/bite/"
dest_folders["ema_readable"] = dest_folder_root+"Eating_readable/ema/"
dest_folders["location_readable"] = dest_folder_root+"Eating_readable/location/"
dest_folders["log_readable"] = dest_folder_root+"Eating_readable/log/"
dest_folders["meal_readable"] = dest_folder_root+"Eating_readable/meal/"

if not os.path.exists(os.path.dirname(source_folder)):
    print("Source folder doesn't exist. Program exited.")
    sys.exit(0)

for key, value in dest_folders.items():
    create_directory(value)


# In[ ]:


to_date_str = ""
window_len_half = 48
window_len = 2 * window_len_half
interval = 1000/16  #in millisecond
smooth_factor = 0.8

x_th_max = -3
min_bite_interval = 40 #samples
var_th = 1

min_meal_process_interval = 30*100 #milliseconds
min_ema_sent_interval = 10*60*1000 #milliseconds
meal_bite_max_distance = 60*1000 #milli second
min_meal_duration = 60*1000 #milliseconds
min_meal_bite_count = 3
min_wait_time_after_meal_to_decide = 4*60*1000 #milliseconds

last_sensor_files = {}
bite_buffer = {}
meal_buffer = {}
last_meal_process_time = {}
last_time_ema_sent = {}


# In[ ]:


model = load_model("eating_model.h5")


# In[ ]:


def process_beacon_data(data, file_info):
    watch_id = file_info["watch_id"]    
    last_beacon_time = 0
    last_battery_time = 0
    last_battery_pct = ""    
    beacon_data = ""
    battery_data =""
    beacon_data_count = 0
    battery_data_count = 0
    
    for s in data:
        tokens = s.rstrip().split(",")
        t = int(tokens[0]) + file_info["reference_time_diff"]
        code = int(tokens[1])
        mac = tokens[2]
        if code == 1:            
            tx_power = tokens[3]
            rssi = tokens[4]
            beacon_data += mac + "," + watch_id +"," + str(t) +","+ tx_power +"," + rssi +"\n"
            beacon_data_count += 1
            last_beacon_time = t           
            
        elif code > 1 and code < 1000:            
            battery_pct = tokens[3]
            battery_data += str(t) +"," + str(code) + "," + mac + "," + battery_pct +"\n"            
            last_battery_time = t
            last_battery_pct = battery_pct
            battery_data_count += 1
            
        else:
            raise Exception("Error in ble file code")
            
    if len(beacon_data)>0:
        tm = datetime.fromtimestamp(last_beacon_time/1000.0)
        print("Beacon reading count:", beacon_data_count, ", Last reading at: ", tm)
        with open(dest_folders["beacon"]+to_date_str+"_beacon_data_"+ watch_id, "a") as file:
            file.write(beacon_data)
            
    if len(battery_data)>0:
        tm = datetime.fromtimestamp(last_battery_time/1000.0)
        print("Battery reading count:", battery_data_count ,", Last reading at: ", tm, ", Pct: ", last_battery_pct)
        with open(dest_folders["battery"]+to_date_str+"_watch_battery_"+ watch_id, "a") as file:
            file.write(battery_data)   


# In[ ]:


def process_sensor_data(data, file_info):    
    global last_sensor_file
    global bite_buffer
    global meal_buffer
    global last_meal_process_time
    global last_time_ema_sent
    watch_id = file_info["watch_id"]
    
    print("Sample size: ", data.shape)
    #print(file_info["reference_time_diff"])
    #print(data[:10, :])    
    data[:, 0] = data[:, 0] + file_info["reference_time_diff"]    
    print("Start, end: ", datetime.fromtimestamp(data[0, 0]/1000), ", ", datetime.fromtimestamp(data[-1, 0]/1000), ", Duration(sec): ", ((data[-1, 0]-data[0, 0])/1000))
    
    bite_str = ""    
    last_data = []
    resample_start_time = data[0, 0]
    last_smooth_sample = data[0, :]    
    
    if watch_id in last_sensor_files:
        last_file = last_sensor_files[watch_id]        
        if last_file["service_start_time_watch"] == file_info["service_start_time_watch"] and last_file["file_index"] == file_info["file_index"] -1:
            print("This is sequence of last file.")            
            last_data = last_file["data_resampled"]
            data = np.concatenate((last_file["last_sample_raw"], data))
            resample_start_time = last_data[-1, 0] + interval
            last_smooth_sample = last_data[-1, :]
    else:
        bite_buffer[watch_id] = []
        meal_buffer[watch_id] = []        
        last_meal_process_time[watch_id] = 0
        last_time_ema_sent[watch_id] = 0
    
    #resample data    
    ts = np.arange(resample_start_time, data[-1, 0], interval)
    count = len(ts)
    resdata = np.zeros((count, 4))  #resampled data  
    j = 0
    for i in range(count):                        
        while not(data[j, 0] <= ts[i] < data[j+1, 0]):
            j+=1        
        resdata[i, 0] = ts[i]
        factor = (ts[i] - data[j, 0])/(data[j+1, 0]-data[j, 0]);
        resdata[i, 1:] = (1-factor)*data[j, 1:]  + factor*data[j+1, 1:]
    
    print("Sample size after resampling: ", resdata.shape)
    
    #smooth data
    last_smooth_sample = last_smooth_sample.reshape((1, 4))
    resdata[0, 1:] = smooth_factor*last_smooth_sample[0, 1:] + (1-smooth_factor)*resdata[0, 1:]
    for i in range(1, count):
        resdata[i, 1:] = smooth_factor*resdata[i-1, 1:] + (1-smooth_factor)*resdata[i, 1:] 
      
    
    #concatening data from last file
    if len(last_data)>0:
        resdata = np.concatenate((last_data[-window_len:, :], resdata))
    
    print("Sample size after concatenate: ", resdata.shape)
        
    #finding potential bite points by x_th
    step_length = min_bite_interval//2
    x = resdata[:, 1]
    count = len(x)
    mp = []
    for i in range(0, count-step_length, step_length):
        min_index = i
        for j in range(i, i+step_length):
            if x[j] < x[min_index]:
                min_index = j
                
        if x[min_index] <= x_th_max and min_index >= window_len_half and min_index < count-window_len_half:
            mp.append(min_index)
    
    print("mp count by x_th: ", len(mp), " >>", mp)
    
    
    #filtering potential bite points by neighbor distance
    while len(mp)>=2:
        res = []            
        ix = mp[0]
        ixRight = mp[1]
        if ixRight - ix > min_bite_interval or x[ix] < x[ixRight]:
            res.append(ix)

        mp_count = len(mp)
        for i in range(1, mp_count - 1):
            ix = mp[i]
            ixLeft = mp[i - 1]
            ixRight = mp[i + 1]

            cond_left = ix - ixLeft > min_bite_interval or x[ix] <= x[ixLeft]
            cond_right = ixRight - ix > min_bite_interval or x[ix] < x[ixRight]
            if cond_left and cond_right:
                res.append(ix)

        ix = mp[mp_count - 1]
        ixLeft = mp[mp_count - 2]
        if ix - ixLeft > min_bite_interval or x[ix] <= x[ixLeft]:
            res.append(ix)            

        if len(mp) == len(res):
            break        
        mp = res 
            
    print("mp count after neighbor filter:", len(mp), " >>", mp)    
    
    #filtering potential bite points by features
    mp_count = len(mp)
    if mp_count>0:
        res = []        
        for i in range(mp_count):
            ix = mp[i]
            v = np.sum(np.var(resdata[ix-window_len_half:ix+window_len_half, 1:], axis = 0))
            if v >= var_th:
                res.append(ix)
        mp = res        
    
    print("mp count after feature filter:", len(mp), " >>", mp)
    
    #extracting and normalizing segments
    bites = []
    mp_count = len(mp)
    if mp_count>0:
        X = np.zeros((mp_count, window_len, 3))
        for i in range(mp_count):
            ix = mp[i]
            d = resdata[ix-window_len_half:ix+window_len_half, 1:]
            sq = np.multiply(d, d)
            mag = np.sqrt(np.sum(sq, axis = 1, keepdims=True))
            X[i, :, :] = np.divide(d, mag)
                
        #predicting bites
        X = X.reshape(X.shape[0], X.shape[1], X.shape[2], 1)
        Ypr = model.predict(X, verbose=0)        
        for i in range(mp_count):
            if Ypr[i, 0]>=0.5:
                ix = mp[i]
                bites.append([resdata[ix, 0], Ypr[i, 0]])                
                bite_str += watch_id + "," + milli_to_str(file_info["upload_time"]) + "," + milli_to_str(int(resdata[ix, 0])) + ",0," + str(Ypr[i, 0])+"\n"
    
    print("Bite count: ", len(bites))
    #print(bite_buffer)
    if len(bites)>0:            
        bite_buffer[watch_id].extend(bites)
        print(bite_str)
        with open(dest_folders["bite"]+to_date_str+"_eating_bite_"+ watch_id, "a") as file:
            file.write(bite_str) 
    
    #storing info and data for next iteration
    file_info["last_sample_raw"] = data[-1, :].reshape(1, 4)
    file_info["data_resampled"] = resdata
    last_sensor_files[watch_id] = file_info
    
    
    


# In[ ]:


def process_meals_ema(watch_id):
    global bite_buffer
    global meal_buffer
    global last_meal_process_time
    global last_time_ema_sent
    
    cur_time = int(time.time()*1000)
    bite_buff = bite_buffer[watch_id]
    if len(bite_buff) < min_meal_bite_count or cur_time - last_meal_process_time[watch_id]< min_meal_process_interval or cur_time - last_time_ema_sent[watch_id] < min_ema_sent_interval:
        return
        
    meal_str = ""
    ema_str = ""
    
    
    last_meal_process_time[watch_id] = cur_time
    clusters = []
    c = []
    c.append(bite_buff[0])
    for i in range(1, len(bite_buff)):
        if bite_buff[i][0] - bite_buff[i-1][0] <= meal_bite_max_distance:
            c.append(bite_buff[i])
        else:
            if len(c) >= min_meal_bite_count:
                clusters.append(c)
            c = []
            c.append(bite_buff[i])
            

    if len(c) >= min_meal_bite_count:
        clusters.append(c)

    #print(clusters)
    if len(clusters) > 0:            
        for i in range(len(clusters)-1):
            c = clusters[i]
            meal_str += watch_id +","+ str(cur_time) +","+ str(c[0][0]) +","+ str(c[-1][0]) +","+ str(len(c)) +"\n"


        c = clusters[-1]
        last_time_for_remove = c[0][0]-1        
        if cur_time - c[-1][0] >= min_wait_time_after_meal_to_decide and c[-1][0] - c[0][0] >= min_meal_duration and len(c)>=min_meal_bite_count:
            meal_str += watch_id +","+ str(cur_time) +","+ str(c[0][0]) +","+ str(c[-1][0]) +","+ str(len(c)) +"\n"            
            last_time_for_remove = c[-1][0]
            
            if (cur_time - c[-1][0]) <= 30*60*1000:
                last_time_ema_sent
                ema_str = watch_id + "," + last_time_ema_sent + "," + last_time_ema_sent +",0\n"
        
        new_buff=[]
        for i in range(len(bite_buff)):
            if bite_buff[i][0] > last_time_for_remove:
                new_buff.append(bite_buff[i])
            
        bite_buffer[watch_id] = bite_buff
                
    if len(meal_str)>0:
        with open(dest_folders["meal"]+to_date_str+"_eating_meal_"+ watch_id, "a") as file:
            file.write(meal_str) 
            
    if len(ema_str)>0:
        with open(dest_folders["ema"]+to_date_str+"_eating_ema_"+ watch_id, "a") as file:
            file.write(ema_str) 
    
    


# In[ ]:


print("****************************************************")
print("****** WELCOME TO M2FED WATCH DATA PROCESSOR  ******")
print("****************************************************")

sleep_count = 0
while True:    
    file_list = os.listdir(source_folder)
    if len(file_list) ==0:
        for key, _ in meal_buffer.items():
            process_meals_ema(key)
        sleep_count += 1
        cur_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        print("Waiting for data. Last checked at: %s"%cur_time, end="\r")
        time.sleep(5)
        continue
              
    to_date_str = datetime.now().strftime("%Y-%m-%d")        
    last_process_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print("\n\n----------------------------------------------------")
    print("Processing at time:", last_process_time, ", File count: ", len(file_list))        
    
    file_list.sort()
    for f in file_list:
        print("\nProcessing file: ", f)        
        tokens = f.rstrip().split("-")
        
        try:
            if len(tokens)!=7:
                raise Exception("File name do not have 7 tokens. It has ", len(tokens), " tokens!")

            file_type = tokens[0]
            file_info = {}        
            file_info["watch_id"] = tokens[1]
            file_info["reference_time_server"] = int(tokens[2])
            file_info["reference_time_watch"] = int(tokens[3])
            file_info["service_start_time_watch"] = int(tokens[4])
            file_info["file_start_time_watch"] = int(tokens[5])
            file_info["file_index"] = int(tokens[6])
            file_info["reference_time_diff"] = file_info["reference_time_server"] - file_info["reference_time_watch"]
            file_info["file_start_time_watch_adjusted"] = file_info["file_start_time_watch"] + file_info["reference_time_diff"]
            file_info["upload_time"] = int(time.time()*1000)
            
        
            if file_type == "sensor":
                data = np.genfromtxt(source_folder+f, delimiter=",")
                process_sensor_data(data, file_info)
            elif file_type == "ble":
                with open(source_folder+f, "r") as file:
                    data = file.readlines()
                process_beacon_data(data, file_info)
            else:
                raise Exception("File starts with unknown word!")
        
            create_directory(dest_folders["rawdata"] + to_date_str + "/")
            os.rename(source_folder+f, dest_folders["rawdata"] + to_date_str + "/" + f)
            
        except Exception as e:
            print("***** Error in file: "+f)            
            print(str(e))
            print(traceback.format_exc())
            os.rename(source_folder+f, dest_folders["rawdata_error"]+f)
            
    for key, _ in meal_buffer.items():
            process_meals_ema(key)
            
    print("\nWaiting for data. Last data process time: ", str(last_process_time))
    sleep_count = 0
        

