function result = read_raw_data_false(names, names_false)
result = true;
fprintf('Reading false raw sign data ... \n');
sub_data_false = [];
k=1;
for i = 1:length(names_false)    
    folder = strcat('sign_data_false/',names_false(i));    
    files = dir(char(folder));

    subject_index = 0;
    for j = 1:length(names)
        if strcmpi(names_false(i), names(j))
            subject_index = j;
            break;
        end
    end

    if subject_index == 0
        display('Subject index not found');            
        result = false;
        return;
    end

    sub_data_false(i).subject_index = subject_index;
    sub_data_false(i).subject_name = names_false(i);

    serial = 1;
    for j=3:length(files)  % first two files are . and ..        
        file_name = files(j).name;        
        file_path = strcat(folder,'/',file_name);       
        fprintf('Reading %d : %s \n', serial, char(file_path));
        st = strsplit(file_name,'_');
        
        if strcmpi(st{2}, names_false(i)) == false
            display('Name doesnt match');
            names(i)
            file_name
            result = false;
            return;
        end
     
        sub_data_false(i).sign(serial).file_name = file_name;
        sub_data_false(i).sign(serial).attacker = st{3};
        sub_data_false(i).sign(serial).attack_type = st{4};
        sub_data_false(i).sign(serial).raw_data = csvread(char(file_path));  
        sub_data_false(i).sign(serial).processed = 0;
        serial = serial + 1;             
    end
    sub_data_false(i).sign_count = serial - 1;
    
end

save('sub_data_false','sub_data_false');

fprintf('Reading raw false sign data done\n\n');