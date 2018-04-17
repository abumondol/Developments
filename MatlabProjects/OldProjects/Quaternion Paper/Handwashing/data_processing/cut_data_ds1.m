folder = 'C:/Users/mm5gg/Box Sync/Data Sets/Hand wash/Hand Wash Mobiquitous/';
src = strcat(folder,'mobiquitous_ds1');
dest = strcat(folder, 'sensys_ds1');

files = dir(src)
for i =3:length(files)
    srcfile = strcat(src, '/', files(i).name)
    destfile = strcat(dest, '/', files(i).name);
    d = csvread(srcfile);
    
    if endsWith(files(i).name, 'mix_hw')
        d = d(300:300+40*50-1, 2:end);
    elseif endsWith(files(i).name, 'not_hw')
        d = d(500:500+110*50-1, 2:end);
    else
        d = d(500:500+10*50-1, 2:end);
    end    
    
    csvwrite(destfile, d);
end

