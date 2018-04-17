raw_data = [];

for i =1:20
    file_path = strcat('C:/ASM/developments/play/myprojects/NetBeansProjects/Eating Detection/gatech_data/my_gatech_data/acls/acl_', num2str(i),'.csv');
    acl = csvread(file_path);
    raw_data(i).acl = check_and_correct_timestamps(acl);
    file_path = strcat('C:/ASM/developments/play/myprojects/NetBeansProjects/Eating Detection/gatech_data/my_gatech_data/annotations/annot_', num2str(i),'.csv');
    raw_data(i).annotations = csvread(file_path);
end

save('raw_data', 'raw_data');
