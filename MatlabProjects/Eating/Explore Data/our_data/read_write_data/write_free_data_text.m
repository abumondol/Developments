load('C:\ASM\DevData\eating\matlab\our_free_data_noneat');
sub_count = length(data);

folder_path = 'C:\ASM\DevData\eating\our_free_data_text';
for subj = 1:sub_count
    accel = data(subj).accel;
    accel_file = strcat(folder_path, '\accel_', num2str(subj-1))
    csvwrite(accel_file, accel);
end