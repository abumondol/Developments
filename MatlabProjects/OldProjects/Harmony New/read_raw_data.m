
names = {'nhw','abu','liza', 'test'};

display('Reading raw Hand Wash  data ...');
hw = {};

% reading handwash data
for i = 1:length(names)    
    folder = strcat('data/',names(i));    
    files = dir(char(folder));

    for j=3:length(files)
        file_name = files(j).name;       
        file_path = strcat(folder,'/',file_name)
        st = strsplit(file_name,'_');

        if strcmpi(names(i),'nhw') == false && strcmpi(st{2},names(i)) == false
            display('Name doesnt match');
            display(names(i));
            return
        end        

        d =  csvread(char(file_path));
        if d(40,1) - d(20,1) > 10000
            d(:,1) = round(d(:,1)/1e6,0);
        end

        hw(i).data(j-2).raw_data = d;
        hw(i).subject_name = names(i);
        hw(i).data(j-2).processed = 0;
        hw(i).data(j-2).file_name = file_name;
    end    
end

save('hw','hw');
display('Reading raw data done');
