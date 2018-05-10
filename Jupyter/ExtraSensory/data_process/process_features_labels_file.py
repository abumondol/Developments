
# coding: utf-8

# In[ ]:


import numpy as np
import os
import pickle


# In[ ]:


src_folder = "C:/ASM/PublicData/extrasensory/ExtraSensory.per_uuid_features_labels"
dest_folder = "C:/ASM/DevData/extrasensory/data"

if not os.path.exists(dest_folder):
    os.makedirs(dest_folder)

file_list = os.listdir(src_folder)
print("total files: ", len(file_list))

s='timestamp,raw_acc:magnitude_stats:mean,raw_acc:magnitude_stats:std,raw_acc:magnitude_stats:moment3,raw_acc:magnitude_stats:moment4,raw_acc:magnitude_stats:percentile25,raw_acc:magnitude_stats:percentile50,raw_acc:magnitude_stats:percentile75,raw_acc:magnitude_stats:value_entropy,raw_acc:magnitude_stats:time_entropy,raw_acc:magnitude_spectrum:log_energy_band0,raw_acc:magnitude_spectrum:log_energy_band1,raw_acc:magnitude_spectrum:log_energy_band2,raw_acc:magnitude_spectrum:log_energy_band3,raw_acc:magnitude_spectrum:log_energy_band4,raw_acc:magnitude_spectrum:spectral_entropy,raw_acc:magnitude_autocorrelation:period,raw_acc:magnitude_autocorrelation:normalized_ac,raw_acc:3d:mean_x,raw_acc:3d:mean_y,raw_acc:3d:mean_z,raw_acc:3d:std_x,raw_acc:3d:std_y,raw_acc:3d:std_z,raw_acc:3d:ro_xy,raw_acc:3d:ro_xz,raw_acc:3d:ro_yz,proc_gyro:magnitude_stats:mean,proc_gyro:magnitude_stats:std,proc_gyro:magnitude_stats:moment3,proc_gyro:magnitude_stats:moment4,proc_gyro:magnitude_stats:percentile25,proc_gyro:magnitude_stats:percentile50,proc_gyro:magnitude_stats:percentile75,proc_gyro:magnitude_stats:value_entropy,proc_gyro:magnitude_stats:time_entropy,proc_gyro:magnitude_spectrum:log_energy_band0,proc_gyro:magnitude_spectrum:log_energy_band1,proc_gyro:magnitude_spectrum:log_energy_band2,proc_gyro:magnitude_spectrum:log_energy_band3,proc_gyro:magnitude_spectrum:log_energy_band4,proc_gyro:magnitude_spectrum:spectral_entropy,proc_gyro:magnitude_autocorrelation:period,proc_gyro:magnitude_autocorrelation:normalized_ac,proc_gyro:3d:mean_x,proc_gyro:3d:mean_y,proc_gyro:3d:mean_z,proc_gyro:3d:std_x,proc_gyro:3d:std_y,proc_gyro:3d:std_z,proc_gyro:3d:ro_xy,proc_gyro:3d:ro_xz,proc_gyro:3d:ro_yz,raw_magnet:magnitude_stats:mean,raw_magnet:magnitude_stats:std,raw_magnet:magnitude_stats:moment3,raw_magnet:magnitude_stats:moment4,raw_magnet:magnitude_stats:percentile25,raw_magnet:magnitude_stats:percentile50,raw_magnet:magnitude_stats:percentile75,raw_magnet:magnitude_stats:value_entropy,raw_magnet:magnitude_stats:time_entropy,raw_magnet:magnitude_spectrum:log_energy_band0,raw_magnet:magnitude_spectrum:log_energy_band1,raw_magnet:magnitude_spectrum:log_energy_band2,raw_magnet:magnitude_spectrum:log_energy_band3,raw_magnet:magnitude_spectrum:log_energy_band4,raw_magnet:magnitude_spectrum:spectral_entropy,raw_magnet:magnitude_autocorrelation:period,raw_magnet:magnitude_autocorrelation:normalized_ac,raw_magnet:3d:mean_x,raw_magnet:3d:mean_y,raw_magnet:3d:mean_z,raw_magnet:3d:std_x,raw_magnet:3d:std_y,raw_magnet:3d:std_z,raw_magnet:3d:ro_xy,raw_magnet:3d:ro_xz,raw_magnet:3d:ro_yz,raw_magnet:avr_cosine_similarity_lag_range0,raw_magnet:avr_cosine_similarity_lag_range1,raw_magnet:avr_cosine_similarity_lag_range2,raw_magnet:avr_cosine_similarity_lag_range3,raw_magnet:avr_cosine_similarity_lag_range4,watch_acceleration:magnitude_stats:mean,watch_acceleration:magnitude_stats:std,watch_acceleration:magnitude_stats:moment3,watch_acceleration:magnitude_stats:moment4,watch_acceleration:magnitude_stats:percentile25,watch_acceleration:magnitude_stats:percentile50,watch_acceleration:magnitude_stats:percentile75,watch_acceleration:magnitude_stats:value_entropy,watch_acceleration:magnitude_stats:time_entropy,watch_acceleration:magnitude_spectrum:log_energy_band0,watch_acceleration:magnitude_spectrum:log_energy_band1,watch_acceleration:magnitude_spectrum:log_energy_band2,watch_acceleration:magnitude_spectrum:log_energy_band3,watch_acceleration:magnitude_spectrum:log_energy_band4,watch_acceleration:magnitude_spectrum:spectral_entropy,watch_acceleration:magnitude_autocorrelation:period,watch_acceleration:magnitude_autocorrelation:normalized_ac,watch_acceleration:3d:mean_x,watch_acceleration:3d:mean_y,watch_acceleration:3d:mean_z,watch_acceleration:3d:std_x,watch_acceleration:3d:std_y,watch_acceleration:3d:std_z,watch_acceleration:3d:ro_xy,watch_acceleration:3d:ro_xz,watch_acceleration:3d:ro_yz,watch_acceleration:spectrum:x_log_energy_band0,watch_acceleration:spectrum:x_log_energy_band1,watch_acceleration:spectrum:x_log_energy_band2,watch_acceleration:spectrum:x_log_energy_band3,watch_acceleration:spectrum:x_log_energy_band4,watch_acceleration:spectrum:y_log_energy_band0,watch_acceleration:spectrum:y_log_energy_band1,watch_acceleration:spectrum:y_log_energy_band2,watch_acceleration:spectrum:y_log_energy_band3,watch_acceleration:spectrum:y_log_energy_band4,watch_acceleration:spectrum:z_log_energy_band0,watch_acceleration:spectrum:z_log_energy_band1,watch_acceleration:spectrum:z_log_energy_band2,watch_acceleration:spectrum:z_log_energy_band3,watch_acceleration:spectrum:z_log_energy_band4,watch_acceleration:relative_directions:avr_cosine_similarity_lag_range0,watch_acceleration:relative_directions:avr_cosine_similarity_lag_range1,watch_acceleration:relative_directions:avr_cosine_similarity_lag_range2,watch_acceleration:relative_directions:avr_cosine_similarity_lag_range3,watch_acceleration:relative_directions:avr_cosine_similarity_lag_range4,watch_heading:mean_cos,watch_heading:std_cos,watch_heading:mom3_cos,watch_heading:mom4_cos,watch_heading:mean_sin,watch_heading:std_sin,watch_heading:mom3_sin,watch_heading:mom4_sin,watch_heading:entropy_8bins,location:num_valid_updates,location:log_latitude_range,location:log_longitude_range,location:min_altitude,location:max_altitude,location:min_speed,location:max_speed,location:best_horizontal_accuracy,location:best_vertical_accuracy,location:diameter,location:log_diameter,location_quick_features:std_lat,location_quick_features:std_long,location_quick_features:lat_change,location_quick_features:long_change,location_quick_features:mean_abs_lat_deriv,location_quick_features:mean_abs_long_deriv,audio_naive:mfcc0:mean,audio_naive:mfcc1:mean,audio_naive:mfcc2:mean,audio_naive:mfcc3:mean,audio_naive:mfcc4:mean,audio_naive:mfcc5:mean,audio_naive:mfcc6:mean,audio_naive:mfcc7:mean,audio_naive:mfcc8:mean,audio_naive:mfcc9:mean,audio_naive:mfcc10:mean,audio_naive:mfcc11:mean,audio_naive:mfcc12:mean,audio_naive:mfcc0:std,audio_naive:mfcc1:std,audio_naive:mfcc2:std,audio_naive:mfcc3:std,audio_naive:mfcc4:std,audio_naive:mfcc5:std,audio_naive:mfcc6:std,audio_naive:mfcc7:std,audio_naive:mfcc8:std,audio_naive:mfcc9:std,audio_naive:mfcc10:std,audio_naive:mfcc11:std,audio_naive:mfcc12:std,audio_properties:max_abs_value,audio_properties:normalization_multiplier,discrete:app_state:is_active,discrete:app_state:is_inactive,discrete:app_state:is_background,discrete:app_state:missing,discrete:battery_plugged:is_ac,discrete:battery_plugged:is_usb,discrete:battery_plugged:is_wireless,discrete:battery_plugged:missing,discrete:battery_state:is_unknown,discrete:battery_state:is_unplugged,discrete:battery_state:is_not_charging,discrete:battery_state:is_discharging,discrete:battery_state:is_charging,discrete:battery_state:is_full,discrete:battery_state:missing,discrete:on_the_phone:is_False,discrete:on_the_phone:is_True,discrete:on_the_phone:missing,discrete:ringer_mode:is_normal,discrete:ringer_mode:is_silent_no_vibrate,discrete:ringer_mode:is_silent_with_vibrate,discrete:ringer_mode:missing,discrete:wifi_status:is_not_reachable,discrete:wifi_status:is_reachable_via_wifi,discrete:wifi_status:is_reachable_via_wwan,discrete:wifi_status:missing,lf_measurements:light,lf_measurements:pressure,lf_measurements:proximity_cm,lf_measurements:proximity,lf_measurements:relative_humidity,lf_measurements:battery_level,lf_measurements:screen_brightness,lf_measurements:temperature_ambient,discrete:time_of_day:between0and6,discrete:time_of_day:between3and9,discrete:time_of_day:between6and12,discrete:time_of_day:between9and15,discrete:time_of_day:between12and18,discrete:time_of_day:between15and21,discrete:time_of_day:between18and24,discrete:time_of_day:between21and3,label:LYING_DOWN,label:SITTING,label:FIX_walking,label:FIX_running,label:BICYCLING,label:SLEEPING,label:LAB_WORK,label:IN_CLASS,label:IN_A_MEETING,label:LOC_main_workplace,label:OR_indoors,label:OR_outside,label:IN_A_CAR,label:ON_A_BUS,label:DRIVE_-_I_M_THE_DRIVER,label:DRIVE_-_I_M_A_PASSENGER,label:LOC_home,label:FIX_restaurant,label:PHONE_IN_POCKET,label:OR_exercise,label:COOKING,label:SHOPPING,label:STROLLING,label:DRINKING__ALCOHOL_,label:BATHING_-_SHOWER,label:CLEANING,label:DOING_LAUNDRY,label:WASHING_DISHES,label:WATCHING_TV,label:SURFING_THE_INTERNET,label:AT_A_PARTY,label:AT_A_BAR,label:LOC_beach,label:SINGING,label:TALKING,label:COMPUTER_WORK,label:EATING,label:TOILET,label:GROOMING,label:DRESSING,label:AT_THE_GYM,label:STAIRS_-_GOING_UP,label:STAIRS_-_GOING_DOWN,label:ELEVATOR,label:OR_standing,label:AT_SCHOOL,label:PHONE_IN_HAND,label:PHONE_IN_BAG,label:PHONE_ON_TABLE,label:WITH_CO-WORKERS,label:WITH_FRIENDS,label_source'
columns = s.split(",")
column_count = len(columns)
print("Column counts: ", column_count)

with open(dest_folder+'/features_labels_column_names.pkl', 'wb') as f:
    pickle.dump(columns, f)
    
so = 'timestamp,original_label:LYING_DOWN,original_label:SITTING,original_label:STANDING_IN_PLACE,original_label:STANDING_AND_MOVING,original_label:WALKING,original_label:RUNNING,original_label:BICYCLING,original_label:LIFTING_WEIGHTS,original_label:PLAYING_BASEBALL,original_label:PLAYING_BASKETBALL,original_label:PLAYING_LACROSSE,original_label:SKATEBOARDING,original_label:PLAYING_SOCCER,original_label:PLAYING_FRISBEE,original_label:EXERCISING,original_label:STRETCHING,original_label:YOGA,original_label:ELLIPTICAL_MACHINE,original_label:TREADMILL,original_label:STATIONARY_BIKE,original_label:COOKING,original_label:CLEANING,original_label:GARDENING,original_label:DOING_LAUNDRY,original_label:MOWING_THE_LAWN,original_label:RAKING_LEAVES,original_label:VACUUMING,original_label:WASHING_DISHES,original_label:WASHING_CAR,original_label:MANUAL_LABOR,original_label:DANCING,original_label:LISTENING_TO_MUSIC__WITH_EARPHONES_,original_label:LISTENING_TO_MUSIC__NO_EARPHONES_,original_label:LISTENING_TO_AUDIO__WITH_EARPHONES_,original_label:LISTENING_TO_AUDIO__NO_EARPHONES_,original_label:PLAYING_MUSICAL_INSTRUMENT,original_label:SINGING,original_label:WHISTLING,original_label:PLAYING_VIDEOGAMES,original_label:PLAYING_PHONE-GAMES,original_label:RELAXING,original_label:STROLLING,original_label:HIKING,original_label:SHOPPING,original_label:WATCHING_TV,original_label:TALKING,original_label:READING_A_BOOK,original_label:DRINKING__ALCOHOL_,original_label:SMOKING,original_label:EATING,original_label:DRINKING__NON-ALCOHOL_,original_label:SLEEPING,original_label:TOILET,original_label:BATHING_-_BATH,original_label:BATHING_-_SHOWER,original_label:GROOMING,original_label:DRESSING,original_label:STAIRS_-_GOING_UP,original_label:STAIRS_-_GOING_DOWN,original_label:LIMPING,original_label:JUMPING,original_label:LAUGHING,original_label:CRYING,original_label:USING_CRUTCHES,original_label:WHEELCHAIR,original_label:LAB_WORK,original_label:WRITTEN_WORK,original_label:DRAWING,original_label:TEXTING,original_label:SURFING_THE_INTERNET,original_label:COMPUTER_WORK,original_label:STUDYING,original_label:IN_CLASS,original_label:IN_A_MEETING,original_label:AT_HOME,original_label:AT_WORK,original_label:AT_SCHOOL,original_label:AT_A_BAR,original_label:AT_A_CONCERT,original_label:AT_A_PARTY,original_label:AT_A_SPORTS_EVENT,original_label:AT_THE_BEACH,original_label:AT_SEA,original_label:AT_THE_POOL,original_label:AT_THE_GYM,original_label:AT_A_RESTAURANT,original_label:OUTSIDE,original_label:INDOORS,original_label:ON_A_BUS,original_label:ON_A_PLANE,original_label:ON_A_TRAIN,original_label:ON_A_BOAT,original_label:ELEVATOR,original_label:MOTORBIKE,original_label:RIDING_AN_ANIMAL,original_label:DRIVE_-_I_M_THE_DRIVER,original_label:DRIVE_-_I_M_A_PASSENGER,original_label:IN_A_CAR,original_label:PHONE_IN_POCKET,original_label:PHONE_IN_HAND,original_label:PHONE_IN_BAG,original_label:PHONE_ON_TABLE,original_label:PHONE_AWAY_FROM_ME,original_label:PHONE_-_SOMEONE_ELSE_USING_IT,original_label:PHONE_STRAPPED,original_label:TRANSFER_-_BED_TO_WHEELCHAIR,original_label:TRANSFER_-_BED_TO_STAND,original_label:TRANSFER_-_WHEELCHAIR_TO_BED,original_label:TRANSFER_-_STAND_TO_BED,original_label:ON_A_DATE,original_label:WITH_CO-WORKERS,original_label:WITH_FAMILY,original_label:WITH_FRIENDS,original_label:WITH_KIDS,original_label:TAKING_CARE_OF_KIDS,original_label:WITH_A_PET,label_source'
columns = so.split(",")
column_count = len(columns)
print("Column counts: ", column_count)

with open(dest_folder+'/original_labels_column_names.pkl', 'wb') as f:
    pickle.dump(columns, f)


# In[ ]:


res = []
uuids = []

for file_name in file_list:
    d = np.genfromtxt(src_folder+'/'+file_name, delimiter=',', skip_header=1)
    print(file_name, d.shape, d[0,0], d[-1,0])
        
    assert column_count == d.shape[1]    
    res.append(d)
    
    uuids.append(file_name.split(".")[0])    


# In[ ]:


with open(dest_folder+'/features_labels.pkl', 'wb') as f:
    pickle.dump(res, f)


# In[ ]:


print(len(uuids))
print(uuids)
s = "\n".join(uuids)
with open('C:/ASM/DevData/extrasensory/data/uuid_list.csv', 'w') as f:
    f.write(s)

