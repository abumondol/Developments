%load('C:\ASM\DevData\eating\matlab\our_lab_data');
sub_count = length(data);

folder_path = 'C:\ASM\DevData\eating\our_data\lab_data';
for subj = 1:sub_count
    accel = data(subj).accel;
    annots = data(subj).annots;
    
    accel_file = strcat(folder_path, '\accel_', num2str(subj-1))
    dlmwrite(accel_file, accel, 'precision','%10.5f');
    
    annot_file = strcat(folder_path, '\annot_', num2str(subj-1));
    dlmwrite(annot_file, annots, 'precision','%10.5f');
    
end