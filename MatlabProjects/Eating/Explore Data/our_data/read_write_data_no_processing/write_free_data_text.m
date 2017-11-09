%load('C:\ASM\DevData\eating\matlab\our_free_data_noneat');
sub_count = length(data);

folder_path = 'C:\ASM\DevData\eating\our_data\free_data';
for subj = 1:sub_count
    accel = data(subj).accel;
    accel_file = strcat(folder_path, '\accel_', num2str(subj-1))
    dlmwrite(accel_file, accel, 'precision','%10.5f');
end