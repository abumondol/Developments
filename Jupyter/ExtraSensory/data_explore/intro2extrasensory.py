
# coding: utf-8

# # Introduction to the <i>ExtraSensory Dataset</i>

# This tutorial will help you get familiar with the <i>ExtraSensory Dataset</i>.<br/>
# With it you'll see how to use the primary data files that include sensor-features and context-labels for each user in the dataset.<p/>
# 
# At first glance, you can quickly go over this notebook as a document, to see what are the different parts of the data and how do labels and features distribute. For deeper familiarity with the data and technically how to use it, go over the specific code of the functions.</p>
# 
# We'll cover:
# <ul>
# <li><a href="#read_user_data">Reading a user's data file</a> and interpreting the different pieces of data.</li>
# <li><a href="#labels">Analyzing the context-labels</a>:<br/>
# What labels are included? How do the reported contexts change through time? What combinations of labels are common?</li>
# <li><a href="#features">Analyzing the sensor-features</a>:<br/>
# Which sensors are included? What features were extracted from the sensors? What are the distributions of feature values?</li>
# <li><a href="#features_labels_dist">Looking at relations between features and labels</a>:<br/>
# How do feature distributions differ when the user was engaged in different contexts?</li>
# <li><a href="#context_recognition">Context recognition</a>: the task of predicting the context-labels based on the sensor-features.<br/>
# This includes:
# <ul>
# <li><a href="#training">Training</a>:<br/>
# fitting a model to training data.</li>
# <li><a href="#testing">Testing</a>:<br/>
# evaluating the correctness of prediction on test data. We start with testing on the same user that was used for training, and continue to the more useful evaluation of generalization - testing on a new user.</li>
# </ul>
# </li>
# </ul>
# <p>
# Written by Yonatan Vaizman, May 2017.
# </p>

# In[1]:


get_ipython().run_line_magic('matplotlib', 'inline')
import matplotlib as mpl;
import matplotlib.pyplot as plt;


# <a name="read_user_data"></a>
# <b>Reading user data</b>.<br/>
# Each user has a separate data file that contains several pieces of the data, mainly features and labels.<br/>
# To read the different data pieces for a user, you can use the following functions:

# In[18]:


import numpy as np;
import gzip;
from io import StringIO;

def parse_header_of_csv(csv_str):
    # Isolate the headline columns:
    headline = csv_str[:csv_str.index('\n')];
    columns = headline.split(',');

    # The first column should be timestamp:
    assert columns[0] == 'timestamp';
    # The last column should be label_source:
    assert columns[-1] == 'label_source';
    
    # Search for the column of the first label:
    for (ci,col) in enumerate(columns):
        if col.startswith('label:'):
            first_label_ind = ci;
            break;
        pass;

    # Feature columns come after timestamp and before the labels:
    feature_names = columns[1:first_label_ind];
    # Then come the labels, till the one-before-last column:
    label_names = columns[first_label_ind:-1];
    for (li,label) in enumerate(label_names):
        # In the CSV the label names appear with prefix 'label:', but we don't need it after reading the data:
        assert label.startswith('label:');
        label_names[li] = label.replace('label:','');
        pass;
    
    return (feature_names,label_names);

def parse_body_of_csv(csv_str,n_features):
    # Read the entire CSV body into a single numeric matrix:
    full_table = np.loadtxt(StringIO.StringIO(csv_str),delimiter=',',skiprows=1);
    
    # Timestamp is the primary key for the records (examples):
    timestamps = full_table[:,0].astype(int);
    
    # Read the sensor features:
    X = full_table[:,1:(n_features+1)];
    
    # Read the binary label values, and the 'missing label' indicators:
    trinary_labels_mat = full_table[:,(n_features+1):-1]; # This should have values of either 0., 1. or NaN
    M = np.isnan(trinary_labels_mat); # M is the missing label matrix
    Y = np.where(M,0,trinary_labels_mat) > 0.; # Y is the label matrix
    
    return (X,Y,M,timestamps);

'''
Read the data (precomputed sensor-features and labels) for a user.
This function assumes the user's data file is present.
'''
def read_user_data(path, uuid):
    user_data_file = path+('%s.features_labels.csv.gz' % uuid);

    # Read the entire csv file of the user:
    with gzip.open(user_data_file,'r') as fid:
        csv_str = fid.read();
        pass;
    print(len(csv_str), type(csv_str))
    (feature_names,label_names) = parse_header_of_csv(csv_str);
    n_features = len(feature_names);
    (X,Y,M,timestamps) = parse_body_of_csv(csv_str,n_features);

    return (X,Y,M,timestamps,feature_names,label_names);


# Now we can read data for a particular user (make sure the user's data file is in this directory):

# In[19]:


path = 'C:/ASM/PublicData/extrasensory/ExtraSensory.per_uuid_features_labels/'
uuid = '1155FF54-63D3-4AB2-9863-8385D0BD0A13';
(X,Y,M,timestamps,feature_names,label_names) = read_user_data(path, uuid);


# Let's start exploring the user's data:

# In[26]:


print "The parts of the user's data (and their dimensions):";


# In[27]:


print "Every example has its timestamp, indicating the minute when the example was recorded";
print "User %s has %d examples (~%d minutes of behavior)" % (uuid,len(timestamps),len(timestamps));
timestamps.shape


# In[28]:


print "The primary data files have %d different sensor-features" % len(feature_names);


# In[29]:


print "X is the feature matrix. Each row is an example and each column is a sensor-feature:";
X.shape


# In[30]:


print "The primary data files have %s context-labels" % len(label_names);


# In[33]:


print "Y is the binary label-matrix. Each row represents an example and each column represents a label.";
print "Value of 1 indicates the label is relevant for the example:";
Y.shape


# In[34]:


print "Y is accompanied by the missing-label-matrix, M.";
print "Value of 1 indicates that it is best to consider an entry (example-label pair) as 'missing':";
M.shape


# ====================================================================================================

# <a name="labels"></a>
# ### The labels:

# In[5]:


n_examples_per_label = np.sum(Y,axis=0);
labels_and_counts = zip(label_names,n_examples_per_label);
sorted_labels_and_counts = sorted(labels_and_counts,reverse=True,key=lambda pair:pair[1]);
print "How many examples does this user have for each contex-label:";
print "-"*20;
for (label,count) in sorted_labels_and_counts:
    print "label %s - %d minutes" % (label,count);
    pass;


# The labels are provided with 'standardized' names, but we can prettify them:

# In[6]:


def get_label_pretty_name(label):
    if label == 'FIX_walking':
        return 'Walking';
    if label == 'FIX_running':
        return 'Running';
    if label == 'LOC_main_workplace':
        return 'At main workplace';
    if label == 'OR_indoors':
        return 'Indoors';
    if label == 'OR_outside':
        return 'Outside';
    if label == 'LOC_home':
        return 'At home';
    if label == 'FIX_restaurant':
        return 'At a restaurant';
    if label == 'OR_exercise':
        return 'Exercise';
    if label == 'LOC_beach':
        return 'At the beach';
    if label == 'OR_standing':
        return 'Standing';
    if label == 'WATCHING_TV':
        return 'Watching TV'
    
    if label.endswith('_'):
        label = label[:-1] + ')';
        pass;
    
    label = label.replace('__',' (').replace('_',' ');
    label = label[0] + label[1:].lower();
    label = label.replace('i m','I\'m');
    return label;


# In[7]:


print "How many examples does this user have for each contex-label:";
print "-"*20;
for (label,count) in sorted_labels_and_counts:
    print "%s - %d minutes" % (get_label_pretty_name(label),count);
    pass;


# ====================================================================================================

# For mutually-exclusive context-labels, we can observe the distribution of time spent<br/>
# (at least, time the user recorded and labeled context):

# In[8]:


def figure__pie_chart(Y,label_names,labels_to_display,title_str,ax):
    portion_of_time = np.mean(Y,axis=0);
    portions_to_display = [portion_of_time[label_names.index(label)] for label in labels_to_display];
    pretty_labels_to_display = [get_label_pretty_name(label) for label in labels_to_display];
    
    ax.pie(portions_to_display,labels=pretty_labels_to_display,autopct='%.2f%%');
    ax.axis('equal');
    plt.title(title_str);
    return;


# In[11]:


print("Since the collection of labels relied on self-reporting in-the-wild, the labeling may be incomplete.");
print("For instance, the users did not always report the position of the phone.");
fig = plt.figure(figsize=(15,5),facecolor='white');

ax1 = plt.subplot(1,2,1);
labels_to_display = ['LYING_DOWN','SITTING','OR_standing','FIX_walking','FIX_running'];
figure__pie_chart(Y,label_names,labels_to_display,'Body state',ax1);

ax2 = plt.subplot(1,2,2);
labels_to_display = ['PHONE_IN_HAND','PHONE_IN_BAG','PHONE_IN_POCKET','PHONE_ON_TABLE'];
figure__pie_chart(Y,label_names,labels_to_display,'Phone position',ax2);


# ====================================================================================================

# <a name="label_combinations"></a>
# Now let's observe how these contexts spread over the user's days of participation?<br/>
# Also, let's see label-combinations that describe specific situations.

# In[12]:


def get_actual_date_labels(tick_seconds):
    import datetime;
    import pytz;
    
    time_zone = pytz.timezone('US/Pacific'); # Assuming the data comes from PST time zone
    weekday_names = ['Mon','Tue','Wed','Thu','Fri','Sat','Sun'];
    datetime_labels = [];
    for timestamp in tick_seconds:
        tick_datetime = datetime.datetime.fromtimestamp(timestamp,tz=time_zone);
        weekday_str = weekday_names[tick_datetime.weekday()];
        time_of_day = tick_datetime.strftime('%I:%M%p');
        datetime_labels.append('%s\n%s' % (weekday_str,time_of_day));
        pass;
    
    return datetime_labels;
    
def figure__context_over_participation_time(timestamps,Y,label_names,labels_to_display,label_colors,use_actual_dates=False):

    fig = plt.figure(figsize=(10,7),facecolor='white');
    ax = plt.subplot(1,1,1);
    
    seconds_in_day = (60*60*24);

    ylabels = [];
    ax.plot(timestamps,len(ylabels)*np.ones(len(timestamps)),'|',color='0.5',label='(Collected data)');
    ylabels.append('(Collected data)');

    for (li,label) in enumerate(labels_to_display):
        lind = label_names.index(label);
        is_label_on = Y[:,lind];
        label_times = timestamps[is_label_on];

        label_str = get_label_pretty_name(label);
        ax.plot(label_times,len(ylabels)*np.ones(len(label_times)),'|',color=label_colors[li],label=label_str);
        ylabels.append(label_str);
        pass;

    tick_seconds = range(timestamps[0],timestamps[-1],seconds_in_day);
    if use_actual_dates:
        tick_labels = get_actual_date_labels(tick_seconds);
        plt.xlabel('Time in San Diego',fontsize=14);
        pass;
    else:
        tick_labels = (np.array(tick_seconds - timestamps[0]).astype(float) / float(seconds_in_day)).astype(int);
        plt.xlabel('days of participation',fontsize=14);
        pass;
    
    ax.set_xticks(tick_seconds);
    ax.set_xticklabels(tick_labels,fontsize=14);

    ax.set_yticks(range(len(ylabels)));
    ax.set_yticklabels(ylabels,fontsize=14);

    ax.set_ylim([-1,len(ylabels)]);
    ax.set_xlim([min(timestamps),max(timestamps)]);
    
    return;


# In[14]:


print("Here is a track of when the user was engaged in different contexts.");
print("The bottom row (gray) states when sensors were recorded (the data-collection app was not running all the time).");
print("The context-labels annotations were self-reported by ther user (and then cleaned by the researchers).")

labels_to_display = ['LYING_DOWN','LOC_home','LOC_main_workplace','SITTING','OR_standing','FIX_walking',                     'IN_A_CAR','ON_A_BUS','EATING'];
label_colors = ['g','y','b','c','m','b','r','k','purple'];
figure__context_over_participation_time(timestamps,Y,label_names,labels_to_display,label_colors);


# Or, if you want to see the day of week and time of day:

# In[15]:


figure__context_over_participation_time(timestamps,Y,label_names,labels_to_display,label_colors,use_actual_dates=True);


# We can analyze the label matrix Y to see overall similarity/co-occurrence relations between labels:

# In[35]:


def jaccard_similarity_for_label_pairs(Y):
    (n_examples,n_labels) = Y.shape;
    Y = Y.astype(int);
    # For each label pair, count cases of:
    # Intersection (co-occurrences) - cases when both labels apply:
    both_labels_counts = np.dot(Y.T,Y);
    # Cases where neither of the two labels applies:
    neither_label_counts = np.dot((1-Y).T,(1-Y));
    # Union - cases where either of the two labels (or both) applies (this is complement of the 'neither' cases):
    either_label_counts = n_examples - neither_label_counts;
    # Jaccard similarity - intersection over union:
    J = np.where(either_label_counts > 0, both_labels_counts.astype(float) / either_label_counts, 0.);
    return J;


# In[36]:


J = jaccard_similarity_for_label_pairs(Y);

print("Label-pairs with higher color value tend to occur together more.");

fig = plt.figure(figsize=(10,10),facecolor='white');
ax = plt.subplot(1,1,1);
plt.imshow(J,interpolation='none');plt.colorbar();

pretty_label_names = [get_label_pretty_name(label) for label in label_names];
n_labels = len(label_names);
ax.set_xticks(range(n_labels));
ax.set_xticklabels(pretty_label_names,rotation=45,ha='right',fontsize=7);
ax.set_yticks(range(n_labels));
ax.set_yticklabels(pretty_label_names,fontsize=7);


# Understandably, 'Sleeping' goes together with 'Lying down'. For this user, 'Walking' occurs a lot with 'Outside' and 'Phone in pocket'...

# ====================================================================================================

# <a name="features"></a>
# ### Sensor features:

# The features are less intuitive to interpret than the labels.<br/>
# They were calculated from the fuller raw-measurements from the various sensors on the smartphone or smartwatch.<p/>
# We can start by looking at the feature names, where the prefix of each feature indicates the sensor where it came from.<br/>
# 
# For convenience, we add a function to parse the feature names and provide the code-name of the sensor they belong to. We'll use the code-names as they appear in the original ExtraSensory paper:<br/>
# <b>Acc</b> (phone-accelerometer), <b>Gyro</b> (phone-gyroscope), <b>WAcc</b> (watch-accelerometer), <b>Loc</b> (location), <b>Aud</b> (audio), and <b>PS</b> (phone-state).<br/>
# Plus, the other sensors provided here that were not analyzed in the original paper: <b>Magnet</b> (phone-magnetometer), <b>Compass</b> (watch-compass), <b>AP</b> (audio properties, about the overall power of the audio), and <b>LF</b> (various low-frequency sensors).

# In[37]:


def get_sensor_names_from_features(feature_names):
    feat_sensor_names = np.array([None for feat in feature_names]);
    for (fi,feat) in enumerate(feature_names):
        if feat.startswith('raw_acc'):
            feat_sensor_names[fi] = 'Acc';
            pass;
        elif feat.startswith('proc_gyro'):
            feat_sensor_names[fi] = 'Gyro';
            pass;
        elif feat.startswith('raw_magnet'):
            feat_sensor_names[fi] = 'Magnet';
            pass;
        elif feat.startswith('watch_acceleration'):
            feat_sensor_names[fi] = 'WAcc';
            pass;
        elif feat.startswith('watch_heading'):
            feat_sensor_names[fi] = 'Compass';
            pass;
        elif feat.startswith('location'):
            feat_sensor_names[fi] = 'Loc';
            pass;
        elif feat.startswith('location_quick_features'):
            feat_sensor_names[fi] = 'Loc';
            pass;
        elif feat.startswith('audio_naive'):
            feat_sensor_names[fi] = 'Aud';
            pass;
        elif feat.startswith('audio_properties'):
            feat_sensor_names[fi] = 'AP';
            pass;
        elif feat.startswith('discrete'):
            feat_sensor_names[fi] = 'PS';
            pass;
        elif feat.startswith('lf_measurements'):
            feat_sensor_names[fi] = 'LF';
            pass;
        else:
            raise ValueError("!!! Unsupported feature name: %s" % feat);

        pass;

    return feat_sensor_names;    


# In[38]:


feat_sensor_names = get_sensor_names_from_features(feature_names);

for (fi,feature) in enumerate(feature_names):
    print("%3d) %s %s" % (fi,feat_sensor_names[fi].ljust(10),feature));
    pass;


# Now we can examine the values of these features (keeping in mind that some may be missing sometimes, represented as NaN):

# In[39]:


def figure__feature_track_and_hist(X,feature_names,timestamps,feature_inds):
    seconds_in_day = (60*60*24);
    days_since_participation = (timestamps - timestamps[0]) / float(seconds_in_day);
    
    for ind in feature_inds:
        feature = feature_names[ind];
        feat_values = X[:,ind];
        
        fig = plt.figure(figsize=(10,3),facecolor='white');
        
        ax1 = plt.subplot(1,2,1);
        ax1.plot(days_since_participation,feat_values,'.-',markersize=3,linewidth=0.1);
        plt.xlabel('days of participation');
        plt.ylabel('feature value');
        plt.title('%d) %s\nfunction of time' % (ind,feature));
        
        ax1 = plt.subplot(1,2,2);
        existing_feature = np.logical_not(np.isnan(feat_values));
        ax1.hist(feat_values[existing_feature],bins=30);
        plt.xlabel('feature value');
        plt.ylabel('count');
        plt.title('%d) %s\nhistogram' % (ind,feature));
        
        pass;
    
    return;


# In[40]:


feature_inds = [0,102,133,148,157,158];
figure__feature_track_and_hist(X,feature_names,timestamps,feature_inds);


# In[41]:


print("The phone-state (PS) features are represented as binary indicators:");
feature_inds = [205,223];
figure__feature_track_and_hist(X,feature_names,timestamps,feature_inds);


# ====================================================================================================

# <a name="features_labels_dist"></a>
# <b>Relation between sensor-features and context-label</b>.<br/>
# We can start looking into how the sensor-feature values correspond to different context labels:

# In[42]:


def figure__feature_scatter_for_labels(X,Y,feature_names,label_names,feature1,feature2,label2color_map):
    feat_ind1 = feature_names.index(feature1);
    feat_ind2 = feature_names.index(feature2);
    example_has_feature1 = np.logical_not(np.isnan(X[:,feat_ind1]));
    example_has_feature2 = np.logical_not(np.isnan(X[:,feat_ind2]));
    example_has_features12 = np.logical_and(example_has_feature1,example_has_feature2);
    
    fig = plt.figure(figsize=(12,5),facecolor='white');
    ax1 = plt.subplot(1,2,1);
    ax2 = plt.subplot(2,2,2);
    ax3 = plt.subplot(2,2,4);
    
    for label in label2color_map.keys():
        label_ind = label_names.index(label);
        pretty_name = get_label_pretty_name(label);
        color = label2color_map[label];
        style = '.%s' % color;
        
        is_relevant_example = np.logical_and(example_has_features12,Y[:,label_ind]);
        count = sum(is_relevant_example);
        feat1_vals = X[is_relevant_example,feat_ind1];
        feat2_vals = X[is_relevant_example,feat_ind2];
        ax1.plot(feat1_vals,feat2_vals,style,markersize=5,label=pretty_name);
        
        ax2.hist(X[is_relevant_example,feat_ind1],bins=20,normed=True,color=color,alpha=0.5,label='%s (%d)' % (pretty_name,count));
        ax3.hist(X[is_relevant_example,feat_ind2],bins=20,normed=True,color=color,alpha=0.5,label='%s (%d)' % (pretty_name,count));
        pass;
    
    ax1.set_xlabel(feature1);
    ax1.set_ylabel(feature2);
    
    ax2.set_title(feature1);
    ax3.set_title(feature2);
    
    ax2.legend(loc='best');
    
    return;


# In[43]:


feature1 = 'proc_gyro:magnitude_stats:time_entropy';#raw_acc:magnitude_autocorrelation:period';
feature2 = 'raw_acc:3d:mean_y';
label2color_map = {'PHONE_IN_HAND':'b','PHONE_ON_TABLE':'g'};
figure__feature_scatter_for_labels(X,Y,feature_names,label_names,feature1,feature2,label2color_map);


# In[44]:


feature1 = 'watch_acceleration:magnitude_spectrum:log_energy_band1';
feature2 = 'watch_acceleration:3d:mean_z';
label2color_map = {'FIX_walking':'b','WATCHING_TV':'g'};
figure__feature_scatter_for_labels(X,Y,feature_names,label_names,feature1,feature2,label2color_map);


# The distributions of sensor-feature in different contexts indicate that these features carry informative signals to differentiate these behavioral contexts.<br/>
# Of course, right now we are only observing data from a single user, and these distributions may be different for whole populations of people.

# ====================================================================================================

# <a name="context_recognition"></a>
# 
# Now, we can try the task of <b>context recognition</b>: predicting the context-labels based on the sensor-features.<p/>
# 
# We'll use linear models, specifically a logistic-regression classifier, to predict a single binary label.<br/>
# We can choose which sensors to use.

# <a name="training"></a>
# Let's start by <b>training</b> a model:

# In[45]:


import sklearn.linear_model;

def project_features_to_selected_sensors(X,feat_sensor_names,sensors_to_use):
    use_feature = np.zeros(len(feat_sensor_names),dtype=bool);
    for sensor in sensors_to_use:
        is_from_sensor = (feat_sensor_names == sensor);
        use_feature = np.logical_or(use_feature,is_from_sensor);
        pass;
    X = X[:,use_feature];
    return X;

def estimate_standardization_params(X_train):
    mean_vec = np.nanmean(X_train,axis=0);
    std_vec = np.nanstd(X_train,axis=0);
    return (mean_vec,std_vec);

def standardize_features(X,mean_vec,std_vec):
    # Subtract the mean, to centralize all features around zero:
    X_centralized = X - mean_vec.reshape((1,-1));
    # Divide by the standard deviation, to get unit-variance for all features:
    # * Avoid dividing by zero, in case some feature had estimate of zero variance
    normalizers = np.where(std_vec > 0., std_vec, 1.).reshape((1,-1));
    X_standard = X_centralized / normalizers;
    return X_standard;

def train_model(X_train,Y_train,M_train,feat_sensor_names,label_names,sensors_to_use,target_label):
    # Project the feature matrix to the features from the desired sensors:
    X_train = project_features_to_selected_sensors(X_train,feat_sensor_names,sensors_to_use);
    print("== Projected the features to %d features from the sensors: %s" % (X_train.shape[1],', '.join(sensors_to_use)));

    # It is recommended to standardize the features (subtract mean and divide by standard deviation),
    # so that all their values will be roughly in the same range:
    (mean_vec,std_vec) = estimate_standardization_params(X_train);
    X_train = standardize_features(X_train,mean_vec,std_vec);
    
    # The single target label:
    label_ind = label_names.index(target_label);
    y = Y_train[:,label_ind];
    missing_label = M_train[:,label_ind];
    existing_label = np.logical_not(missing_label);
    
    # Select only the examples that are not missing the target label:
    X_train = X_train[existing_label,:];
    y = y[existing_label];
    
    # Also, there may be missing sensor-features (represented in the data as NaN).
    # You can handle those by imputing a value of zero (since we standardized, this is equivalent to assuming average value).
    # You can also further select examples - only those that have values for all the features.
    # For this tutorial, let's use the simple heuristic of zero-imputation:
    X_train[np.isnan(X_train)] = 0.;
    
    print("== Training with %d examples. For label '%s' we have %d positive and %d negative examples." %           (len(y),get_label_pretty_name(target_label),sum(y),sum(np.logical_not(y))) );
    
    # Now, we have the input features and the ground truth for the output label.
    # We can train a logistic regression model.
    
    # Typically, the data is highly imbalanced, with many more negative examples;
    # To avoid a trivial classifier (one that always declares 'no'), it is important to counter-balance the pos/neg classes:
    lr_model = sklearn.linear_model.LogisticRegression(class_weight='balanced');
    lr_model.fit(X_train,y);
    
    # Assemble all the parts of the model:
    model = {            'sensors_to_use':sensors_to_use,            'target_label':target_label,            'mean_vec':mean_vec,            'std_vec':std_vec,            'lr_model':lr_model};
    
    return model;


# In[46]:


sensors_to_use = ['Acc','WAcc'];
target_label = 'FIX_walking';
model = train_model(X,Y,M,feat_sensor_names,label_names,sensors_to_use,target_label);


# <a name="testing"></a>
# <b>Testing</b>.<br/>
# Now we can evaluate the quality of the classifier.<br/>
# Let's test the predictions it makes on the user:

# In[52]:


def test_model(X_test,Y_test,M_test,timestamps,feat_sensor_names,label_names,model):
    # Project the feature matrix to the features from the sensors that the classifier is based on:
    X_test = project_features_to_selected_sensors(X_test,feat_sensor_names,model['sensors_to_use']);
    print("== Projected the features to %d features from the sensors: %s" % (X_test.shape[1],', '.join(model['sensors_to_use'])));

    # We should standardize the features the same way the train data was standardized:
    X_test = standardize_features(X_test,model['mean_vec'],model['std_vec']);
    
    # The single target label:
    label_ind = label_names.index(model['target_label']);
    y = Y_test[:,label_ind];
    missing_label = M_test[:,label_ind];
    existing_label = np.logical_not(missing_label);
    
    # Select only the examples that are not missing the target label:
    X_test = X_test[existing_label,:];
    y = y[existing_label];
    timestamps = timestamps[existing_label];

    # Do the same treatment for missing features as done to the training data:
    X_test[np.isnan(X_test)] = 0.;
    
    print("== Testing with %d examples. For label '%s' we have %d positive and %d negative examples." %           (len(y),get_label_pretty_name(target_label),sum(y),sum(np.logical_not(y))) );
    
    # Preform the prediction:
    y_pred = model['lr_model'].predict(X_test);
    
    # Naive accuracy (correct classification rate):
    accuracy = np.mean(y_pred == y);
    
    # Count occorrences of true-positive, true-negative, false-positive, and false-negative:
    tp = np.sum(np.logical_and(y_pred,y));
    tn = np.sum(np.logical_and(np.logical_not(y_pred),np.logical_not(y)));
    fp = np.sum(np.logical_and(y_pred,np.logical_not(y)));
    fn = np.sum(np.logical_and(np.logical_not(y_pred),y));
    
    # Sensitivity (=recall=true positive rate) and Specificity (=true negative rate):
    sensitivity = float(tp) / (tp+fn);
    specificity = float(tn) / (tn+fp);
    
    # Balanced accuracy is a more fair replacement for the naive accuracy:
    balanced_accuracy = (sensitivity + specificity) / 2.;
    
    # Precision:
    # Beware from this metric, since it may be too sensitive to rare labels.
    # In the ExtraSensory Dataset, there is large skew among the positive and negative classes,
    # and for each label the pos/neg ratio is different.
    # This can cause undesirable and misleading results when averaging precision across different labels.
    precision = float(tp) / (tp+fp);
    
    print("-"*10);
    print('Accuracy*:         %.2f' % accuracy);
    print('Sensitivity (TPR): %.2f' % sensitivity);
    print('Specificity (TNR): %.2f' % specificity);
    print('Balanced accuracy: %.2f' % balanced_accuracy);
    print('Precision**:       %.2f' % precision);
    print("-"*10);
    
    print('* The accuracy metric is misleading - it is dominated by the negative examples (typically there are many more negatives).')
    print('** Precision is very sensitive to rare labels. It can cause misleading results when averaging precision over different labels.')
    
    fig = plt.figure(figsize=(10,4),facecolor='white');
    ax = plt.subplot(1,1,1);
    ax.plot(timestamps[y],1.4*np.ones(sum(y)),'|g',markersize=10,label='ground truth');
    ax.plot(timestamps[y_pred],np.ones(sum(y_pred)),'|b',markersize=10,label='prediction');
    
    seconds_in_day = (60*60*24);
    tick_seconds = range(timestamps[0],timestamps[-1],seconds_in_day);
    tick_labels = (np.array(tick_seconds - timestamps[0]).astype(float) / float(seconds_in_day)).astype(int);
    
    ax.set_ylim([0.5,5]);
    ax.set_xticks(tick_seconds);
    ax.set_xticklabels(tick_labels);
    plt.xlabel('days of participation',fontsize=14);
    ax.legend(loc='best');
    plt.title('%s\nGround truth vs. predicted' % get_label_pretty_name(model['target_label']));
    
    return;


# In[53]:


test_model(X,Y,M,timestamps,feat_sensor_names,label_names,model);


# ====================================================================================================

# Testing recognition on the same user that was used for training is expected to yield high performance.<br/>
# The real challenge is generalization: how well can the trained model recognize context for an unseen user.<p/>
# 
# Let's test the same model on data of another user:

# In[54]:


def validate_column_names_are_consistent(old_column_names,new_column_names):
    if len(old_column_names) != len(new_column_names):
        raise ValueError("!!! Inconsistent number of columns.");
        
    for ci in range(len(old_column_names)):
        if old_column_names[ci] != new_column_names[ci]:
            raise ValueError("!!! Inconsistent column %d) %s != %s" % (ci,old_column_names[ci],new_column_names[ci]));
        pass;
    return;


# In[55]:


uuid = '11B5EC4D-4133-4289-B475-4E737182A406';
(X2,Y2,M2,timestamps2,feature_names2,label_names2) = read_user_data(uuid);

# All the user data files should have the exact same columns. We can validate it:
validate_column_names_are_consistent(feature_names,feature_names2);
validate_column_names_are_consistent(label_names,label_names2);


# In[56]:


test_model(X2,Y2,M2,timestamps2,feat_sensor_names,label_names,model);


# As expected, prediction over a new user is harder.<p/>
# 
# In our papers, we perform 5-fold cross validation evaluation: for each fold training on data from 4/5 of the users and testing on the remaining 1/5 of the users.<br/>
# We also provide files with the same partition (of 60 users to 5 folds) that we used.

# Enjoy!
