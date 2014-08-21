package com.dpcat237.nps.helper;

import android.content.Context;

import com.dpcat237.nps.constant.FileConstants;
import com.dpcat237.nps.model.SongPart;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class FileHelper {
    private static final String TAG = "NPS:FileHelper";
    //MAIN_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath()+ FileConstants.FOLDER_APP;
    private static Integer RECORDER_SAMPLERATE;

    public static String getSongPath(Context context, String songName) {
        File voicesFolder = getVoicesFolder(context);

        return voicesFolder.getAbsolutePath()+"/"+songName;
    }

    public static File getVoicesFolder(Context context) {
        return context.getExternalFilesDir(FileConstants.FOLDER_VOICES);
    }

    public static void deleteFolders(File voiceFolder) {
        //delete voices folder
        File[] songs = voiceFolder.listFiles();
        deleteFiles(songs);
        if (voiceFolder.exists()) {
            voiceFolder.delete();
        }
    }

    private static void deleteFiles(File[] files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static Boolean mergeSongParts(Context context, ArrayList<SongPart> songParts, String finalFile) {
        String voiceFolder = getVoicesFolder(context).getAbsolutePath()+"/";
        try {
            DataOutputStream amplifyOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(finalFile)));
            DataInputStream[] mergeFilesStream = new DataInputStream[songParts.size()];
            long[] sizes=new long[songParts.size()];
            for(int i=0; i<songParts.size(); i++) {
                File file = new File(voiceFolder+songParts.get(i).getFilename());
                sizes[i] = (file.length()-44)/2;
            }
            for(int i =0; i<songParts.size(); i++) {
                mergeFilesStream[i] =new DataInputStream(new BufferedInputStream(new FileInputStream(voiceFolder+songParts.get(i).getFilename())));
                if(i == songParts.size()-1) {
                    mergeFilesStream[i].skip(24);
                    byte[] sampleRt = new byte[4];
                    mergeFilesStream[i].read(sampleRt);
                    ByteBuffer bbInt = ByteBuffer.wrap(sampleRt).order(ByteOrder.LITTLE_ENDIAN);
                    RECORDER_SAMPLERATE = bbInt.getInt();
                    mergeFilesStream[i].skip(16);
                }
                else {
                    mergeFilesStream[i].skip(44);
                }
            }

            for(int b=0; b<songParts.size(); b++) {
                for(int i=0; i<(int)sizes[b]; i++) {
                    byte[] dataBytes = new byte[2];
                    try {
                        dataBytes[0] = mergeFilesStream[b].readByte();
                        dataBytes[1] = mergeFilesStream[b].readByte();
                    }
                    catch (EOFException e) {
                        amplifyOutputStream.close();
                    }
                    short dataInShort = ByteBuffer.wrap(dataBytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
                    float dataInFloat= (float) dataInShort/37268.0f;


                    short outputSample = (short)(dataInFloat * 37268.0f);
                    byte[] dataFin = new byte[2];
                    dataFin[0] = (byte) (outputSample & 0xff);
                    dataFin[1] = (byte)((outputSample >> 8) & 0xff);
                    amplifyOutputStream.write(dataFin, 0 , 2);

                }
            }
            amplifyOutputStream.close();
            for(int i=0; i<songParts.size(); i++) {
                mergeFilesStream[i].close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return false;
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        long size =0;
        try {
            FileInputStream fileSize = new FileInputStream(finalFile);
            size = fileSize.getChannel().size();
            fileSize.close();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();

            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            return false;
        }


        final int RECORDER_BPP = 16;
        long datasize = size + 36;
        long byteRate = (RECORDER_BPP * RECORDER_SAMPLERATE) / 8;
        long longSampleRate = RECORDER_SAMPLERATE;
        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (datasize & 0xff);
        header[5] = (byte) ((datasize >> 8) & 0xff);
        header[6] = (byte) ((datasize >> 16) & 0xff);
        header[7] = (byte) ((datasize >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) 1;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) ((RECORDER_BPP) / 8);  // block align
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (size & 0xff);
        header[41] = (byte) ((size >> 8) & 0xff);
        header[42] = (byte) ((size >> 16) & 0xff);
        header[43] = (byte) ((size >> 24) & 0xff);
        // out.write(header, 0, 44);

        try {
            RandomAccessFile rFile = new RandomAccessFile(finalFile, "rw");
            rFile.seek(0);
            rFile.write(header);
            rFile.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public static void removeSongParts(ArrayList<SongPart> parts, String voiceFolder) {
        for (SongPart part : parts) {
            File file = new File(voiceFolder+part.getFilename());
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
