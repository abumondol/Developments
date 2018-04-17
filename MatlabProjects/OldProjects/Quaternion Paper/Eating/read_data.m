data_folder = 'C:/Users/mm5gg/Box Sync/Data Sets/quaternion ubicomp 2017/eating/data';
annot_folder = 'C:/Users/mm5gg/Box Sync/Data Sets/quaternion ubicomp 2017/eating/annotations_processed';
data_files = dir(data_folder);
annot_files = dir(annot_folder);
times = csvread('C:/Users/mm5gg/Box Sync/Data Sets/quaternion ubicomp 2017/eating/times.csv');

data = [];
sub_count = length(data_files)
for s=3:sub_count
    data_file = data_files(s).name
    annot_file = annot_files(s).name 
    t = times(s-2, :)
    
    d = csvread(strcat(data_folder,'/',data_file));
    annots = csvread(strcat(annot_folder,'/',annot_file)); 
    annots = sortrows(annots);
    
    d = process_sensor_data_grav_acl_aligned(d);
    d(:,1) = d(:,1) + t(1); % add offset
    d = d(d(:,1)>=t(2)*60 & d(:,1)<t(3)*60, :); % take only eating data from combo data
    
    annot_count = length(annots);
    annot_indices = zeros(annot_count, 1);
    
    t = [d(1:end-1,1), d(2:end, 1)];
    for i = 1:annot_count
        a = annots(i,1);
        ix= find(t(:,1)<=a & t(:,2)>a);        
        annot_indices(i, 1) = ix;        
    end
    
    
    data(s-2).data = d;
    data(s-2).annots = [annot_indices, annots];
    data(s-2).times = t;
    
end

save('data','data')

