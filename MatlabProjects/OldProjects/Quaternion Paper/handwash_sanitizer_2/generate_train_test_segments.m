for ic = 1:5
    for s = 1:10
        fprintf('%d, %d\n', ic, s);
        train_cells = [];
        all_ca =[];
        for i = 1:10
            cf = gen_data(s).ico(ic).train_cell_freq;

            hw_ca = cell_assign(ic).subject(i).hw_ca;
            eat_ca = cell_assign(ic).subject(i).eat_ca;
            
            segs = [];
            count = length(hw_ca);            
            six = 0;
            eix = 0;
            startFlag = false;
            for j = 1:count
                ix = hw_ca(j);
                if cf(ix)>0 && startFlag==false
                    six= j;
                    startFlag = true;
                elseif cf(ix)==0 && startFlag==true
                    eix= j-1;
                    startFlag = false;
                    segs = [segs; six, eix];
                end
                
            end
            
            gen_data(s).ico(ic).segments(i).hw = segs;

            segs = [];
            count = length(eat_ca);            
            six = 0;
            eix = 0;
            startFlag = false;
            for j = 1:count
                ix = eat_ca(j);
                if cf(ix)>0 && startFlag==false
                    six= j;
                    startFlag = true;
                elseif cf(ix)==0 && startFlag==true
                    eix= j-1;
                    startFlag = false;
                    segs = [segs; six, eix];
                end
                
            end
            
            gen_data(s).ico(ic).segments(i).eat = segs;

        end

    end
end

save('gen_data', 'gen_data');