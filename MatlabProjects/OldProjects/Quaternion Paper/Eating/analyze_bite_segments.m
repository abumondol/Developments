close all
ico_number = 4;
bps = [];
for s=1:11
    
    d = data(s).data;
    annots = data(s).annots;
    annot_count = length(annots);
    
    bp = zeros(annot_count, 4);
    seg = cell(annot_count, 1);    
    for i = 1:annot_count
        ix = annots(i,1);
        t = annots(i,2);                
        bp(i, 1:3) = d(ix, 2:4);
        bp(i, 4) = annots(i,2);        
        seg{i} = d(ix-200:ix+200, 2:4);
    end
    
    bps = [bps; bp];
    fprintf('%d\n',length(bps));    
    
end

spmo_plot_on_sphere([], bps, ico, ico_number, 'bite points');

