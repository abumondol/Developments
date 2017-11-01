load('C:\ASM\DevData\eating\matlab\our_lab_data');
sub_count = length(data);

folder_path = 'C:\ASM\DevData\eating\our_lab_data_text';
for subj = 1:sub_count
    accel = data(subj).accel;
    annots = data(subj).annots;
    
    accel_file = strcat(folder_path, '\accel_', num2str(subj-1))
    csvwrite(accel_file, accel);
    
    if ~isempty(annots)
        annot_file = strcat(folder_path, '\annot_', num2str(subj-1));
        csvwrite(annot_file, annots);
    end
    
end