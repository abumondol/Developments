load('data');
sub_count = length(data);

folder_path = 'C:\ASM\developments\JavaProjects\Eating Analysis DTW\mydata';
accel_path = strcat(folder_path, '\accel_data');
annot_path = strcat(folder_path, '\annot_data');

for subj = 1:sub_count
    accel = data(subj).accel;
    annots = data(subj).annots;
    
    accel_file = strcat(accel_path, '\accel_', num2str(subj-1))
    csvwrite(accel_file, accel);
    
    if ~isempty(annots)
        annot_file = strcat(annot_path, '\annots_', num2str(subj-1));
        csvwrite(annot_file, annots);
    end
    
end