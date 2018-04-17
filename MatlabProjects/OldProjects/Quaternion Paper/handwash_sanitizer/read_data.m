data_folder = 'C:/Users/mm5gg/Box Sync/Data Sets/quaternion ubicomp 2017/handwash/data';
annot_folder = 'C:/Users/mm5gg/Box Sync/Data Sets/quaternion ubicomp 2017/handwash/annotations_processed';
data_files = dir(data_folder);
annot_files = dir(annot_folder);
times = csvread('C:/Users/mm5gg/Box Sync/Data Sets/quaternion ubicomp 2017/handwash/times.csv');

data = [];
sub_count = length(data_files)
for s=3:sub_count
    data_file = data_files(s).name
    annot_file = annot_files(s).name 
    t = times(s-2, :)
    
    d = csvread(strcat(data_folder,'/',data_file));
    annots = csvread(strcat(annot_folder,'/',annot_file)); 
    annots = sortrows(annots);
    et = annots(end,1)
    
    annots = annots(1:end-1, :);
    sts = [];
    ets = [];
    for i = 1:length(annots)
        if mod(i,2)==1
            sts = [sts; annots(i,1)];
        else
            ets = [ets; annots(i,1)];
        end
    end
    annots = [sts, ets];
    
    d = process_sensor_data_grav_acl_aligned(d);
    d(:,1) = d(:,1) + t(1); % add offset
    eat_data = d(d(:,1)>=t(2)*60 & d(:,1)<t(3)*60, :); % take only eating data from combo data
    hw_data = d(d(:,1)<=et, :); % take only exercise data from combo data

    annot_count = length(annots); 
    annot_indices = zeros(annot_count, 2);
    t = [d(1:end-1,1), d(2:end, 1)];    
    for i = 1:annot_count
        a = annots(i,1);
        ix= find(t(:,1)<=a & t(:,2)>a);        
        annot_indices(i, 1) = ix;        
    
        a = annots(i,2);
        ix= find(t(:,1)<=a & t(:,2)>a);        
        annot_indices(i, 2) = ix;        
    end

    data(s-2).hw_data = hw_data;
    data(s-2).eat_data = eat_data;
    data(s-2).annot_indices = annot_indices;
    data(s-2).annot_times = annots;
    data(s-2).et = et;
    data(s-2).data_file = data_file;
    data(s-2).annot_file = annot_file;
    
end

save('data','data')

